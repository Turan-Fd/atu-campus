const http = require("node:http");
const fs = require("node:fs");
const path = require("node:path");
const os = require("node:os");
let firebaseAdmin = null;
try {
  firebaseAdmin = require("firebase-admin");
} catch {
  firebaseAdmin = null;
}

const PORT = Number(process.env.PORT || 8080);
const dataDirectory = path.join(__dirname, "data");
const statisticsStudentsPath = path.join(dataDirectory, "statistika-students.json");
const legacyStudentsPath = path.join(dataDirectory, "students.json");
const studentPhotosManifestPath = path.join(dataDirectory, "student-photos.json");
const campusContentPath = path.join(dataDirectory, "campus-content.json");
const campusUploadDirectory = path.join(dataDirectory, "campus-uploads");
const campusNotificationsPath = path.join(dataDirectory, "campus-notifications.json");
const campusRoomsPath = path.join(dataDirectory, "campus-chat-rooms.json");
const campusMessagesPath = path.join(dataDirectory, "campus-chat-messages.json");
const campusDeviceTokensPath = path.join(dataDirectory, "campus-device-tokens.json");
const faceAuthSessionsPath = path.join(dataDirectory, "face-auth-sessions.json");
const faceAuthAuditPath = path.join(dataDirectory, "face-auth-audit.json");
const NEWS_ADMIN_ACCESS_CODE = process.env.ATU_NEWS_ADMIN_CODE || "1970103";
const SMS_ADMIN_ACCESS_CODE = process.env.ATU_SMS_ADMIN_CODE || "899913";
const ADMIN_PASSWORD = process.env.ATU_NEWS_ADMIN_PASSWORD || "ATU@1970";
const FIREBASE_SERVICE_ACCOUNT_PATH = process.env.FIREBASE_SERVICE_ACCOUNT_PATH || "";
const FIREBASE_PROJECT_ID = process.env.FIREBASE_PROJECT_ID || "";
const FACE_AUTH_SESSION_EXPIRY_MS = Number(process.env.FACE_AUTH_SESSION_EXPIRY_MS || 180000);
const FACE_VERIFICATION_SERVICE_URL = String(process.env.FACE_VERIFICATION_SERVICE_URL || "").trim();

const loginStudents = fs.existsSync(legacyStudentsPath)
  ? JSON.parse(fs.readFileSync(legacyStudentsPath, "utf8"))
  : [];
const students = fs.existsSync(statisticsStudentsPath)
  ? JSON.parse(fs.readFileSync(statisticsStudentsPath, "utf8"))
  : loginStudents;
const studentPhotosDirectory =
  process.env.STUDENT_PHOTOS_DIR || path.join(os.homedir(), "Documents", "student photos");

if (!fs.existsSync(campusUploadDirectory)) {
  fs.mkdirSync(campusUploadDirectory, { recursive: true });
}

function readJsonFile(filePath, fallback) {
  if (!fs.existsSync(filePath)) {
    fs.writeFileSync(filePath, JSON.stringify(fallback, null, 2), "utf8");
    return structuredClone(fallback);
  }
  try {
    return JSON.parse(fs.readFileSync(filePath, "utf8"));
  } catch {
    return structuredClone(fallback);
  }
}

function writeJsonFile(filePath, data) {
  fs.writeFileSync(filePath, JSON.stringify(data, null, 2), "utf8");
}

function normalizeWorkNumber(value) {
  const digits = String(value || "").replace(/\D/g, "").replace(/^0+/, "");
  return digits || "0";
}

function normalize(value) {
  return String(value || "")
    .toLocaleLowerCase("az-AZ")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/ə/g, "e")
    .replace(/ı/g, "i")
    .replace(/ö/g, "o")
    .replace(/ü/g, "u")
    .replace(/ş/g, "s")
    .replace(/ç/g, "c")
    .replace(/ğ/g, "g")
    .replace(/[^a-z0-9]+/g, " ")
    .trim();
}

function normalizeGroup(group) {
  return normalize(group).replace(/\s+/g, "-");
}

function buildStudentIndex(items) {
  const index = new Map();
  for (const student of items) {
    const key = normalizeWorkNumber(student.workNumber);
    if (!index.has(key)) index.set(key, []);
    index.get(key).push(student);
  }
  return index;
}

const studentsByWorkNumber = buildStudentIndex(students);
const photoManifest = fs.existsSync(studentPhotosManifestPath)
  ? JSON.parse(fs.readFileSync(studentPhotosManifestPath, "utf8"))
  : {};
const photosByWorkNumber = new Map(
  Object.entries(photoManifest).map(([workNumber, relativePath]) => [
    normalizeWorkNumber(workNumber),
    /^https?:\/\//i.test(String(relativePath || ""))
      ? String(relativePath || "")
      : path.join(studentPhotosDirectory, relativePath)
  ])
);

const adminSessions = new Map();

function getFirebaseMessaging() {
  if (!firebaseAdmin || !FIREBASE_SERVICE_ACCOUNT_PATH || !FIREBASE_PROJECT_ID) return null;
  if (!firebaseAdmin.apps.length) {
    const serviceAccount = JSON.parse(fs.readFileSync(FIREBASE_SERVICE_ACCOUNT_PATH, "utf8"));
    firebaseAdmin.initializeApp({
      credential: firebaseAdmin.credential.cert(serviceAccount),
      projectId: FIREBASE_PROJECT_ID
    });
  }
  return firebaseAdmin.messaging();
}

function readCampusContent() {
  return readJsonFile(campusContentPath, []);
}

function writeCampusContent(items) {
  writeJsonFile(campusContentPath, items);
}

function readNotifications() {
  return readJsonFile(campusNotificationsPath, []);
}

function writeNotifications(items) {
  writeJsonFile(campusNotificationsPath, items);
}

function readDeviceTokens() {
  return readJsonFile(campusDeviceTokensPath, []);
}

function writeDeviceTokens(items) {
  writeJsonFile(campusDeviceTokensPath, items);
}

function readFaceAuthSessions() {
  return readJsonFile(faceAuthSessionsPath, []);
}

function writeFaceAuthSessions(items) {
  writeJsonFile(faceAuthSessionsPath, items);
}

function readFaceAuthAudit() {
  return readJsonFile(faceAuthAuditPath, []);
}

function appendFaceAuthAudit(entry) {
  const items = readFaceAuthAudit();
  items.unshift(entry);
  writeJsonFile(faceAuthAuditPath, items.slice(0, 5000));
}

function readChatRooms() {
  return readJsonFile(campusRoomsPath, [officialRoom()]);
}

function writeChatRooms(items) {
  writeJsonFile(campusRoomsPath, items);
}

function readChatMessages() {
  return readJsonFile(campusMessagesPath, []);
}

function writeChatMessages(items) {
  writeJsonFile(campusMessagesPath, items);
}

function detectUploadExtension(mimeType = "", fallbackName = "") {
  if (mimeType === "image/png") return ".png";
  if (mimeType === "image/webp") return ".webp";
  if (mimeType === "image/gif") return ".gif";
  if (mimeType === "video/mp4") return ".mp4";
  if (mimeType === "application/pdf") return ".pdf";
  const fromName = path.extname(fallbackName || "").toLowerCase();
  return fromName || ".jpg";
}

function saveCampusUpload(base64Data, mimeType, originalName = "") {
  if (!base64Data) return "";
  const extension = detectUploadExtension(mimeType, originalName);
  const fileName = `campus_${Date.now()}_${Math.random().toString(36).slice(2, 8)}${extension}`;
  fs.writeFileSync(
    path.join(campusUploadDirectory, fileName),
    Buffer.from(base64Data, "base64")
  );
  return `/campus-upload/${encodeURIComponent(fileName)}`;
}

function publicStudentPhotoPath(workNumber) {
  const reference = photosByWorkNumber.get(normalizeWorkNumber(workNumber));
  if (!reference) return "";
  return /^https?:\/\//i.test(reference)
    ? reference
    : `/student-photo/${encodeURIComponent(normalizeWorkNumber(workNumber))}`;
}

function publicStudent(student) {
  return {
    id: normalizeWorkNumber(student.workNumber),
    name: student.name,
    surname: student.surname,
    fatherName: student.fatherName,
    fin: student.fin || "",
    identityCard: student.identityCard || "",
    username: normalizeWorkNumber(student.workNumber),
    group: student.group,
    faculty: student.faculty,
    department: student.studyForm,
    specialty: student.specialization || student.specialty,
    course: student.course,
    studyForm: student.studyForm,
    educationLevel: student.level,
    status: student.status,
    photoPath: publicStudentPhotoPath(student.workNumber)
  };
}

function createFaceAuthSession(student) {
  const now = Date.now();
  const session = {
    id: `face_${now.toString(36)}_${Math.random().toString(36).slice(2, 8)}`,
    studentId: normalizeWorkNumber(student.workNumber),
    createdAt: now,
    expiresAt: now + FACE_AUTH_SESSION_EXPIRY_MS,
    attempts: 0,
    status: "PENDING",
    referenceImageUrl: publicStudentPhotoPath(student.workNumber),
    challenge: {
      type: "blink_and_turn_left",
      expiresInSeconds: Math.floor(FACE_AUTH_SESSION_EXPIRY_MS / 1000)
    }
  };
  const sessions = readFaceAuthSessions().filter(item => item.expiresAt > now && item.status === "PENDING");
  sessions.unshift(session);
  writeFaceAuthSessions(sessions.slice(0, 2000));
  return session;
}

function findFaceAuthSession(sessionId) {
  const now = Date.now();
  const sessions = readFaceAuthSessions();
  const session = sessions.find(item => item.id === sessionId) || null;
  if (!session) return null;
  if (session.expiresAt <= now) return null;
  return session;
}

function updateFaceAuthSession(sessionId, updater) {
  const sessions = readFaceAuthSessions();
  const index = sessions.findIndex(item => item.id === sessionId);
  if (index < 0) return null;
  const updated = updater({ ...sessions[index] });
  sessions[index] = updated;
  writeFaceAuthSessions(sessions);
  return updated;
}

function stripBase64Prefix(value) {
  const stringValue = String(value || "").trim();
  const match = stringValue.match(/^data:.*?;base64,(.*)$/i);
  return match ? match[1] : stringValue;
}

function contentTypeFromExtension(filePath) {
  const extension = path.extname(String(filePath || "")).toLowerCase();
  if (extension === ".png") return "image/png";
  if (extension === ".webp") return "image/webp";
  if (extension === ".gif") return "image/gif";
  return "image/jpeg";
}

async function readReferenceImagePayload(studentId) {
  const reference = photosByWorkNumber.get(normalizeWorkNumber(studentId));
  if (!reference) return null;

  if (/^https?:\/\//i.test(reference)) {
    const remoteResponse = await fetch(reference);
    if (!remoteResponse.ok) {
      throw new Error(`Reference photo fetch failed with status ${remoteResponse.status}`);
    }
    const contentType = remoteResponse.headers.get("content-type") || "image/jpeg";
    const arrayBuffer = await remoteResponse.arrayBuffer();
    return {
      base64: Buffer.from(arrayBuffer).toString("base64"),
      mimeType: contentType
    };
  }

  if (!fs.existsSync(reference)) {
    return null;
  }

  return {
    base64: fs.readFileSync(reference).toString("base64"),
    mimeType: contentTypeFromExtension(reference)
  };
}

async function runFaceVerificationInference({ session, captures, challengeMeta }) {
  const referenceImage = await readReferenceImagePayload(session.studentId);
  if (!referenceImage) {
    return {
      available: false,
      verified: false,
      reason: "reference_image_not_found"
    };
  }

  if (!FACE_VERIFICATION_SERVICE_URL) {
    return {
      available: false,
      verified: false,
      reason: "face_verification_service_not_configured"
    };
  }

  const response = await fetch(`${FACE_VERIFICATION_SERVICE_URL.replace(/\/$/, "")}/verify`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      referenceImageBase64: referenceImage.base64,
      referenceImageMimeType: referenceImage.mimeType,
      captureImages: captures.map(stripBase64Prefix),
      challengeMeta
    })
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(`Face verification service failed with status ${response.status}`);
  }
  return {
    available: true,
    ...data
  };
}

function faceFailureMessage(reason) {
  switch (String(reason || "").toUpperCase()) {
    case "FACE_MISMATCH":
      return "Üz uyğunluğu təsdiqlənmədi. Yalnız hesab sahibinin üzü ilə davam edin.";
    case "CHALLENGE_INCOMPLETE":
      return "Canlılıq addımları tamamlanmadı. Göz qırpıb başınızı sola çevirərək yenidən yoxlayın.";
    case "SPOOF_SUSPECTED":
      return "Canlı üz tələb olunur. Foto, ekran və ya başqa cihazla giriş qəbul edilmir.";
    case "LOW_IMAGE_QUALITY":
      return "Kamera görüntüsü kifayət qədər aydın deyil. İşıqlı mühitdə üzünüzü sabit saxlayın.";
    case "LIVENESS_FAILED":
      return "Canlılıq təsdiqlənmədi. Kamera qarşısında birbaşa baxaraq yenidən cəhd edin.";
    default:
      return "Üz doğrulaması uğursuz oldu.";
  }
}

function exactMatch(payload) {
  const suppliedCandidates = Array.isArray(payload.candidates) ? payload.candidates : [];
  const candidates = [payload.cardNumber, payload.id, ...suppliedCandidates]
    .map(normalizeWorkNumber)
    .filter(value => value !== "0");
  const matches = [...new Set(candidates)]
    .map(cardNumber => ({
      cardNumber,
      students: studentsByWorkNumber.get(cardNumber) || []
    }))
    .filter(match => match.students.length > 0);

  if (matches.length === 0) return { status: "NOT_FOUND" };
  if (matches.length > 1 || matches[0].students.length !== 1) {
    return { status: "AMBIGUOUS", cardNumber: matches[0].cardNumber };
  }
  return {
    status: "VERIFIED",
    cardNumber: matches[0].cardNumber,
    student: matches[0].students[0]
  };
}

function buildAssistantPrompt(question) {
  return [
    {
      role: "system",
      content:
        "Sen Azerbaycan Texnologiya Universitetinin resmi ATU Campus AI komekchisisən. " +
        "Yalniz universitet, telebe heyati, campus xidmetleri, xeberler, tedris ve ATU ile bagli suallara cavab ver. " +
        "Cavablarin Azerbaycan dilinde, qisa, aydin, resmi ve faydali olsun. " +
        "Resmi menbe lazimdisa atu.edu.az saytini esas menbe kimi qeyd et. " +
        "Universitete aid olmayan movzuda nezaketle bildir ki, yalniz ATU ile bagli suallara komek edirsən."
    },
    {
      role: "user",
      content: String(question || "")
    }
  ];
}

async function askOpenAi(question) {
  const apiKey = process.env.OPENAI_API_KEY || "";
  if (!apiKey) {
    return {
      success: true,
      answer:
        "AI köməkçi backend-də hələ aktiv edilməyib. OPENAI_API_KEY təyin ediləndən sonra real cavablar işləyəcək."
    };
  }

  const response = await fetch("https://api.openai.com/v1/chat/completions", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${apiKey}`
    },
    body: JSON.stringify({
      model: process.env.OPENAI_MODEL || "gpt-4o-mini",
      messages: buildAssistantPrompt(question),
      max_tokens: 520,
      temperature: 0.35
    })
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    console.error("OpenAI error", response.status, data);
    return {
      success: false,
      error: "Hazırda AI cavabı hazırlamaq mümkün olmadı."
    };
  }

  const answer = data?.choices?.[0]?.message?.content || "";
  return {
    success: true,
    answer: String(answer).trim() || "Cavab boş gəldi. Zəhmət olmasa sualı yenidən yazın."
  };
}

function readBody(request) {
  return new Promise(resolve => {
    let body = "";
    request.on("data", chunk => {
      body += chunk;
      if (body.length > 12 * 1024 * 1024) request.destroy();
    });
    request.on("end", () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch {
        resolve({});
      }
    });
  });
}

function sendJson(response, status, body) {
  response.writeHead(status, {
    "Content-Type": "application/json; charset=utf-8",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET,POST,DELETE,OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type,Authorization"
  });
  response.end(JSON.stringify(body));
}

function adminRoleForCode(accessCode) {
  if (accessCode === NEWS_ADMIN_ACCESS_CODE) return "NEWS_ADMIN";
  if (accessCode === SMS_ADMIN_ACCESS_CODE) return "SMS_ADMIN";
  return "";
}

function createSession(role) {
  const token = `${Date.now().toString(36)}-${Math.random().toString(36).slice(2)}-${Math.random().toString(36).slice(2)}`;
  adminSessions.set(token, role);
  return token;
}

function getAdminRole(request) {
  const authorization = String(request.headers.authorization || "");
  if (!authorization.startsWith("Bearer ")) return "";
  return adminSessions.get(authorization.slice(7)) || "";
}

function requireAdmin(response, request, allowedRoles) {
  const role = getAdminRole(request);
  if (!role || !allowedRoles.includes(role)) {
    sendJson(response, 401, { success: false, message: "Admin sessiyasi etibarsızdır." });
    return "";
  }
  return role;
}

function createNotification({
  type = "NEWS",
  title,
  body,
  audienceType = "ALL",
  audienceId = "",
  imageUrl = "",
  attachmentName = "",
  roomId = "",
  messageId = "",
  mediaType = ""
}) {
  const items = readNotifications();
  const notification = {
    id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    type,
    title,
    body,
    audienceType,
    audienceId,
    imageUrl,
    attachmentName,
    roomId,
    messageId,
    mediaType,
    createdAt: Date.now()
  };
  items.unshift(notification);
  writeNotifications(items.slice(0, 3000));
  Promise.resolve(sendPushNotification(notification)).catch(() => {});
  return notification;
}

function officialRoom() {
  return {
    id: "atu-official",
    title: "ATU Rəsmi Qrup",
    subtitle: "Yalnız administrator paylaşımı",
    kind: "OFFICIAL",
    readOnly: true,
    group: ""
  };
}

function ensureRoom(room) {
  const rooms = readChatRooms();
  if (!rooms.some(item => item.id === room.id)) {
    rooms.push(room);
    writeChatRooms(rooms);
  }
  return room;
}

function ensureGroupRoom(groupName) {
  const group = String(groupName || "").trim();
  if (!group) return null;
  const normalizedGroup = normalizeGroup(group);
  const room = {
    id: `group-${normalizedGroup}`,
    title: `${group} qrupu`,
    subtitle: "Qrup daxili canlı söhbət",
    kind: "GROUP",
    readOnly: false,
    group
  };
  return ensureRoom(room);
}

function getStudentById(studentId) {
  const matches = studentsByWorkNumber.get(normalizeWorkNumber(studentId)) || [];
  if (matches.length !== 1) return null;
  return matches[0];
}

function roomsForStudent(studentId) {
  const student = getStudentById(studentId);
  if (!student) return [officialRoom()];
  const rooms = [ensureRoom(officialRoom())];
  const groupRoom = ensureGroupRoom(student.group);
  if (groupRoom) rooms.push(groupRoom);
  return rooms;
}

function roomForStudent(roomId, studentId) {
  return roomsForStudent(studentId).find(room => room.id === roomId) || null;
}

function audienceStudentIdsForRoom(room) {
  if (!room) return [];
  if (room.kind === "OFFICIAL") {
    return allKnownStudentIds();
  }
  if (room.kind === "GROUP") {
    return students
      .filter(student => normalizeGroup(student.group) === normalizeGroup(room.group))
      .map(student => normalizeWorkNumber(student.workNumber))
      .filter(item => item !== "0");
  }
  return [];
}

function createChatMessage({
  roomId,
  senderId,
  senderName,
  senderPhotoPath = "",
  senderRole = "STUDENT",
  text,
  mediaUrl = "",
  mediaType = "",
  attachmentName = ""
}) {
  const messages = readChatMessages();
  const message = {
    id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    roomId,
    senderId,
    senderName,
    senderPhotoPath,
    senderRole,
    text,
    mediaUrl,
    mediaType,
    attachmentName,
    createdAt: Date.now(),
    editedAt: 0,
    reactions: []
  };
  messages.push(message);
  writeChatMessages(messages.slice(-5000));
  return message;
}

function mirrorContentToOfficialRoom(item) {
  ensureRoom(officialRoom());
  createChatMessage({
    roomId: "atu-official",
    senderId: "atu-admin",
    senderName: "ATU Rəsmi Kanal",
    senderRole: "ADMIN",
    text: `${item.title}\n\n${item.summary}`,
    mediaUrl: item.imageUrl,
    mediaType: item.imageUrl ? "image/*" : "",
    attachmentName: item.type === "EVENT" ? "Tədbir paylaşımı" : item.type === "ANNOUNCEMENT" ? "Elan" : "Xəbər"
  });
}

function visibleNotificationsForStudent(studentId) {
  return readNotifications()
    .filter(item => item.audienceType === "ALL" || item.audienceId === normalizeWorkNumber(studentId))
    .sort((a, b) => Number(b.createdAt) - Number(a.createdAt));
}

function allKnownStudentIds() {
  return [...new Set(students.map(student => normalizeWorkNumber(student.workNumber)).filter(item => item !== "0"))];
}

function tokensForStudentIds(studentIds) {
  const allowed = new Set(studentIds.map(normalizeWorkNumber));
  return readDeviceTokens()
    .filter(item => allowed.has(normalizeWorkNumber(item.studentId)) && item.token)
    .map(item => item.token);
}

async function sendPushNotification(item) {
  const messaging = getFirebaseMessaging();
  if (!messaging) return;
  const studentIds = item.audienceType === "ALL"
    ? allKnownStudentIds()
    : [item.audienceId];
  const tokens = [...new Set(tokensForStudentIds(studentIds))];
  if (tokens.length === 0) return;

  const dataPayload = {
    id: String(item.id || ""),
    type: String(item.type || ""),
    title: String(item.title || ""),
    body: String(item.body || ""),
    imageUrl: String(item.imageUrl || ""),
    attachmentName: String(item.attachmentName || ""),
    createdAt: String(item.createdAt || Date.now()),
    roomId: String(item.roomId || ""),
    messageId: String(item.messageId || ""),
    mediaType: String(item.mediaType || "")
  };

  await Promise.all(tokens.map(async token => {
    try {
      await messaging.send({
        token,
        notification: {
          title: item.title,
          body: item.body
        },
        android: {
          priority: "high",
          notification: {
            channelId: "atu_campus_content"
          }
        },
        data: dataPayload
      });
    } catch {
      const remaining = readDeviceTokens().filter(entry => entry.token !== token);
      writeDeviceTokens(remaining);
    }
  }));
}

function visibleMessagesForRoom(roomId) {
  return readChatMessages()
    .filter(item => item.roomId === roomId)
    .sort((a, b) => Number(a.createdAt) - Number(b.createdAt));
}

const server = http.createServer(async (request, response) => {
  const url = new URL(request.url, `http://${request.headers.host}`);

  if (request.method === "OPTIONS") {
    return sendJson(response, 200, { ok: true });
  }

  if (request.method === "GET" && url.pathname === "/health") {
    return sendJson(response, 200, {
      ok: true,
      service: "ATU Campus backend",
      students: students.length,
      uniqueWorkNumbers: studentsByWorkNumber.size,
      indexedPhotos: photosByWorkNumber.size
    });
  }

  if (request.method === "GET" && url.pathname.startsWith("/student-photo/")) {
    const workNumber = normalizeWorkNumber(decodeURIComponent(url.pathname.split("/").pop() || ""));
    const photoPath = photosByWorkNumber.get(workNumber);
    if (!photoPath) {
      return sendJson(response, 404, { message: "Tələbə fotosu tapılmadı." });
    }
    if (/^https?:\/\//i.test(photoPath)) {
      response.writeHead(302, {
        Location: photoPath,
        "Access-Control-Allow-Origin": "*"
      });
      return response.end();
    }
    if (!fs.existsSync(photoPath)) {
      return sendJson(response, 404, { message: "Tələbə fotosu tapılmadı." });
    }
    const extension = path.extname(photoPath).toLowerCase();
    const contentType = extension === ".png"
      ? "image/png"
      : extension === ".webp"
        ? "image/webp"
        : "image/jpeg";
    response.writeHead(200, {
      "Content-Type": contentType,
      "Cache-Control": "private, no-store",
      "Access-Control-Allow-Origin": "*"
    });
    return fs.createReadStream(photoPath).pipe(response);
  }

  if (request.method === "GET" && url.pathname === "/students") {
    const query = normalize(url.searchParams.get("query"));
    const matches = students
      .filter(student => normalize(`${student.name} ${student.surname} ${student.workNumber} ${student.group}`).includes(query))
      .slice(0, 20)
      .map(publicStudent);
    return sendJson(response, 200, { matches });
  }

  if (request.method === "GET" && url.pathname.startsWith("/campus-upload/")) {
    const fileName = path.basename(decodeURIComponent(url.pathname.split("/").pop() || ""));
    const filePath = path.join(campusUploadDirectory, fileName);
    if (!fs.existsSync(filePath)) {
      return sendJson(response, 404, { message: "Fayl tapılmadı." });
    }
    const extension = path.extname(filePath).toLowerCase();
    const contentType =
      extension === ".png" ? "image/png" :
      extension === ".webp" ? "image/webp" :
      extension === ".gif" ? "image/gif" :
      extension === ".mp4" ? "video/mp4" :
      extension === ".pdf" ? "application/pdf" :
      "image/jpeg";
    response.writeHead(200, {
      "Content-Type": contentType,
      "Cache-Control": "private, no-store",
      "Access-Control-Allow-Origin": "*"
    });
    return fs.createReadStream(filePath).pipe(response);
  }

  if (request.method === "GET" && url.pathname === "/campus-content") {
    const requestedType = String(url.searchParams.get("type") || "").toUpperCase();
    const since = Number(url.searchParams.get("since") || 0);
    const items = readCampusContent()
      .filter(item => !requestedType || requestedType === "ALL" || item.type === requestedType)
      .filter(item => !since || Number(item.createdAt) > since)
      .sort((a, b) => Number(b.createdAt) - Number(a.createdAt));
    return sendJson(response, 200, { items });
  }

  if (request.method === "GET" && url.pathname === "/notifications") {
    const studentId = normalizeWorkNumber(url.searchParams.get("studentId"));
    if (studentId === "0") {
      return sendJson(response, 400, { items: [], message: "studentId tələb olunur." });
    }
    return sendJson(response, 200, { items: visibleNotificationsForStudent(studentId) });
  }

  if (request.method === "POST" && url.pathname === "/device-token/register") {
    const payload = await readBody(request);
    const studentId = normalizeWorkNumber(payload.studentId);
    const token = String(payload.token || "").trim();
    if (studentId === "0" || !token) {
      return sendJson(response, 400, { success: false, message: "studentId və token tələb olunur." });
    }
    const items = readDeviceTokens().filter(item => item.token !== token);
    items.unshift({ studentId, token, updatedAt: Date.now() });
    writeDeviceTokens(items.slice(0, 5000));
    return sendJson(response, 200, { success: true });
  }

  if (request.method === "POST" && url.pathname === "/device-token/unregister") {
    const payload = await readBody(request);
    const token = String(payload.token || "").trim();
    if (!token) {
      return sendJson(response, 400, { success: false, message: "token tələb olunur." });
    }
    const items = readDeviceTokens().filter(item => item.token !== token);
    writeDeviceTokens(items);
    return sendJson(response, 200, { success: true });
  }

  if (request.method === "GET" && url.pathname === "/chat/rooms") {
    const studentId = normalizeWorkNumber(url.searchParams.get("studentId"));
    if (studentId === "0") {
      return sendJson(response, 400, { rooms: [], message: "studentId tələb olunur." });
    }
    const rooms = roomsForStudent(studentId).map(room => ({
      ...room,
      lastMessage: visibleMessagesForRoom(room.id).slice(-1)[0] || null
    }));
    return sendJson(response, 200, { rooms });
  }

  if (request.method === "GET" && url.pathname === "/chat/messages") {
    const roomId = String(url.searchParams.get("roomId") || "").trim();
    const since = Number(url.searchParams.get("since") || 0);
    if (!roomId) {
      return sendJson(response, 400, { messages: [], message: "roomId tələb olunur." });
    }
    const messages = visibleMessagesForRoom(roomId)
      .filter(item => !since || Number(item.createdAt) > since || Number(item.editedAt) > since);
    return sendJson(response, 200, { messages });
  }

  if (request.method === "POST" && url.pathname === "/admin/login") {
    const payload = await readBody(request);
    const accessCode = String(payload.accessCode || "").trim();
    const password = String(payload.password || "");
    const role = adminRoleForCode(accessCode);
    if (!role || password !== ADMIN_PASSWORD) {
      return sendJson(response, 401, {
        authenticated: false,
        message: "Admin kodu və ya şifrə yanlışdır."
      });
    }
    const token = createSession(role);
    return sendJson(response, 200, { authenticated: true, token, role });
  }

  if (request.method === "POST" && url.pathname === "/admin/content") {
    const role = requireAdmin(response, request, ["NEWS_ADMIN"]);
    if (!role) return;
    const payload = await readBody(request);
    const type = String(payload.type || "").toUpperCase();
    const title = String(payload.title || "").trim();
    const summary = String(payload.summary || "").trim();
    const body = String(payload.body || "").trim();
    const imageUrl = String(payload.imageUrl || "").trim();
    const imageBase64 = String(payload.imageBase64 || "").trim();
    const imageMimeType = String(payload.imageMimeType || "").trim();
    const imageName = String(payload.imageName || "").trim();
    if (!["NEWS", "ANNOUNCEMENT", "EVENT"].includes(type)) {
      return sendJson(response, 400, { success: false, message: "Məzmun tipi yanlışdır." });
    }
    if (title.length < 4 || body.length < 8) {
      return sendJson(response, 400, { success: false, message: "Başlıq və mətn daha dolğun olmalıdır." });
    }
    const storedImageUrl = imageBase64 ? saveCampusUpload(imageBase64, imageMimeType, imageName) : imageUrl;
    const item = {
      id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
      type,
      title,
      summary: summary || body.slice(0, 180),
      body,
      imageUrl: storedImageUrl,
      author: "ATU News Admin",
      createdAt: Date.now()
    };
    const items = readCampusContent();
    items.unshift(item);
    writeCampusContent(items.slice(0, 500));
    createNotification({
      type,
      title,
      body: item.summary,
      audienceType: "ALL",
      imageUrl: storedImageUrl
    });
    mirrorContentToOfficialRoom(item);
    return sendJson(response, 201, { success: true, item });
  }

  if (request.method === "POST" && url.pathname === "/admin/direct-notification") {
    const role = requireAdmin(response, request, ["SMS_ADMIN", "NEWS_ADMIN"]);
    if (!role) return;
    const payload = await readBody(request);
    const title = String(payload.title || "").trim();
    const body = String(payload.body || "").trim();
    const type = String(payload.type || "DIRECT").toUpperCase();
    const studentIds = Array.isArray(payload.studentIds) ? payload.studentIds.map(normalizeWorkNumber).filter(item => item !== "0") : [];
    const attachmentName = String(payload.attachmentName || "").trim();
    const attachmentMimeType = String(payload.attachmentMimeType || "").trim();
    const attachmentBase64 = String(payload.attachmentBase64 || "").trim();
    const attachmentUrl = attachmentBase64 ? saveCampusUpload(attachmentBase64, attachmentMimeType, attachmentName) : "";
    if (title.length < 3 || body.length < 4 || studentIds.length === 0) {
      return sendJson(response, 400, { success: false, message: "Başlıq, mətn və ən azı bir tələbə seçilməlidir." });
    }
    const created = studentIds.map(studentId =>
      createNotification({
        type,
        title,
        body,
        audienceType: "STUDENT",
        audienceId: studentId,
        imageUrl: attachmentUrl,
        attachmentName,
        mediaType: attachmentMimeType
      })
    );
    return sendJson(response, 201, {
      success: true,
      message: `${created.length} tələbəyə bildiriş göndərildi.`,
      items: created
    });
  }

  if (request.method === "DELETE" && url.pathname.startsWith("/admin/content/")) {
    const role = requireAdmin(response, request, ["NEWS_ADMIN"]);
    if (!role) return;
    const id = decodeURIComponent(url.pathname.split("/").pop() || "");
    const items = readCampusContent();
    const nextItems = items.filter(item => item.id !== id);
    writeCampusContent(nextItems);
    return sendJson(response, 200, { success: true, removed: items.length - nextItems.length });
  }

  if (request.method === "POST" && url.pathname === "/chat/message") {
    const payload = await readBody(request);
    const roomId = String(payload.roomId || "").trim();
    const text = String(payload.text || "").trim();
    const studentId = normalizeWorkNumber(payload.studentId);
    const imageBase64 = String(payload.imageBase64 || "").trim();
    const imageMimeType = String(payload.imageMimeType || "").trim();
    const imageName = String(payload.imageName || "").trim();
    const adminRole = getAdminRole(request);
    const student = getStudentById(studentId);
    let room = null;

    if (adminRole) {
      const rooms = readChatRooms();
      room = rooms.find(item => item.id === roomId) || null;
    } else if (studentId !== "0") {
      room = roomForStudent(roomId, studentId);
    }

    if (!room) {
      return sendJson(response, 404, { success: false, message: "Söhbət otağı tapılmadı." });
    }
    if (!adminRole && room.readOnly) {
      return sendJson(response, 403, { success: false, message: "Bu rəsmi qrupda yalnız reaksiyalar açıqdır." });
    }
    if (text.length < 1 && !imageBase64) {
      return sendJson(response, 400, { success: false, message: "Boş mesaj göndərilə bilməz." });
    }
    const mediaUrl = imageBase64 ? saveCampusUpload(imageBase64, imageMimeType, imageName) : "";
    const message = createChatMessage({
      roomId,
      senderId: adminRole ? `admin-${adminRole}` : studentId,
      senderName: adminRole ? "ATU Admin" : `${student?.name || ""} ${student?.surname || ""}`.trim() || "Tələbə",
      senderPhotoPath: student && publicStudent(student).photoPath ? publicStudent(student).photoPath : "",
      senderRole: adminRole || "STUDENT",
      text,
      mediaUrl,
      mediaType: imageMimeType,
      attachmentName: imageName
    });
    audienceStudentIdsForRoom(room)
      .filter(targetStudentId => targetStudentId !== studentId)
      .forEach(targetStudentId => {
        createNotification({
          type: "DIRECT",
          title: room.title,
          body: text || "Media mesajı göndərildi.",
          audienceType: "STUDENT",
          audienceId: targetStudentId,
          imageUrl: mediaUrl,
          attachmentName: imageName,
          roomId: room.id,
          messageId: message.id,
          mediaType: imageMimeType
        });
      });
    return sendJson(response, 201, {
      success: true,
      message: "Mesaj göndərildi.",
      item: message
    });
  }

  if (request.method === "POST" && url.pathname === "/chat/reaction") {
    const payload = await readBody(request);
    const messageId = String(payload.messageId || "").trim();
    const emoji = String(payload.emoji || "").trim();
    const studentId = normalizeWorkNumber(payload.studentId);
    const student = getStudentById(studentId);
    if (!messageId || !emoji || !student) {
      return sendJson(response, 400, { success: false, message: "Reaksiya üçün mesaj, emoji və tələbə lazımdır." });
    }
    const messages = readChatMessages();
    const index = messages.findIndex(item => item.id === messageId);
    if (index < 0) {
      return sendJson(response, 404, { success: false, message: "Mesaj tapılmadı." });
    }
    const entry = {
      emoji,
      userId: studentId,
      userName: `${student.name} ${student.surname}`.trim(),
      photoPath: publicStudent(student).photoPath
    };
    const reactions = Array.isArray(messages[index].reactions) ? messages[index].reactions : [];
    const sameIndex = reactions.findIndex(item => item.userId === studentId && item.emoji === emoji);
    messages[index].reactions =
      sameIndex >= 0
        ? reactions.filter((_, currentIndex) => currentIndex !== sameIndex)
        : [...reactions.filter(item => item.userId !== studentId || item.emoji !== emoji), entry];
    writeChatMessages(messages);
    return sendJson(response, 200, { success: true, reactions: messages[index].reactions });
  }

  if (request.method === "POST" && url.pathname === "/chat/message-action") {
    const payload = await readBody(request);
    const messageId = String(payload.messageId || "").trim();
    const action = String(payload.action || "").trim();
    const text = String(payload.text || "").trim();
    const studentId = normalizeWorkNumber(payload.studentId);
    const adminRole = getAdminRole(request);
    const messages = readChatMessages();
    const index = messages.findIndex(item => item.id === messageId);
    if (index < 0) {
      return sendJson(response, 404, { success: false, message: "Mesaj tapılmadı." });
    }
    const canManage = adminRole || messages[index].senderId === studentId;
    if (!canManage) {
      return sendJson(response, 403, { success: false, message: "Bu mesaj üçün icazə yoxdur." });
    }
    if (action === "delete") {
      const nextMessages = messages.filter(item => item.id !== messageId);
      writeChatMessages(nextMessages);
      return sendJson(response, 200, { success: true, removed: true });
    }
    if (action === "edit") {
      if (text.length < 1) {
        return sendJson(response, 400, { success: false, message: "Yeni mətn boş ola bilməz." });
      }
      messages[index].text = text;
      messages[index].editedAt = Date.now();
      writeChatMessages(messages);
      return sendJson(response, 200, {
        success: true,
        message: "Mesaj yeniləndi.",
        item: messages[index]
      });
    }
    return sendJson(response, 400, { success: false, message: "Dəstək olmayan əməliyyat." });
  }

  if (request.method === "POST" && url.pathname === "/auth/face/start") {
    const payload = await readBody(request);
    const studentNumber = normalizeWorkNumber(payload.studentNumber);
    if (studentNumber === "0") {
      return sendJson(response, 400, {
        success: false,
        message: "Tələbə nömrəsi tələb olunur."
      });
    }

    const matches = studentsByWorkNumber.get(studentNumber) || [];
    if (matches.length === 0) {
      appendFaceAuthAudit({
        id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        event: "FACE_AUTH_START_NOT_FOUND",
        studentId: studentNumber,
        createdAt: Date.now()
      });
      return sendJson(response, 404, {
        success: false,
        code: "NOT_FOUND",
        message: "Bu nömrə ilə tələbə tapılmadı."
      });
    }

    if (matches.length !== 1) {
      appendFaceAuthAudit({
        id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        event: "FACE_AUTH_START_AMBIGUOUS",
        studentId: studentNumber,
        createdAt: Date.now()
      });
      return sendJson(response, 409, {
        success: false,
        code: "AMBIGUOUS",
        message: "Bu nömrə ilə bir neçə qeyd tapıldı."
      });
    }

    const student = matches[0];
    const session = createFaceAuthSession(student);
    appendFaceAuthAudit({
      id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
      event: "FACE_AUTH_START_CREATED",
      sessionId: session.id,
      studentId: session.studentId,
      createdAt: Date.now()
    });

    return sendJson(response, 200, {
      success: true,
      sessionId: session.id,
      challenge: session.challenge,
      studentPreview: {
        fullName: `${student.name || ""} ${student.surname || ""}`.trim(),
        group: student.group || ""
      }
    });
  }

  if (request.method === "POST" && url.pathname === "/auth/face/complete") {
    const payload = await readBody(request);
    const sessionId = String(payload.sessionId || "").trim();
    const captures = Array.isArray(payload.captures) ? payload.captures.filter(Boolean) : [];
    const challengeMeta = payload.challengeMeta && typeof payload.challengeMeta === "object"
      ? payload.challengeMeta
      : {};

    if (!sessionId) {
      return sendJson(response, 400, {
        success: false,
        message: "sessionId tələb olunur."
      });
    }

    const session = findFaceAuthSession(sessionId);
    if (!session) {
      return sendJson(response, 404, {
        success: false,
        code: "SESSION_NOT_FOUND",
        message: "Üz yoxlama sessiyası tapılmadı və ya vaxtı bitib."
      });
    }

    const updatedSession = updateFaceAuthSession(sessionId, current => ({
      ...current,
      attempts: Number(current.attempts || 0) + 1,
      lastAttemptAt: Date.now()
    }));
    const student = getStudentById(session.studentId);

    try {
      const inference = await runFaceVerificationInference({
        session,
        captures,
        challengeMeta
      });

      const verified = Boolean(inference.available && inference.verified);
      updateFaceAuthSession(sessionId, current => ({
        ...current,
        status: verified ? "VERIFIED" : "FAILED",
        completedAt: Date.now(),
        lastResult: {
          verified,
          faceSimilarity: Number(inference.faceSimilarity || 0),
          livenessScore: Number(inference.livenessScore || 0),
          antiSpoofLabel: String(inference.antiSpoofLabel || ""),
          confidenceBand: String(inference.confidenceBand || ""),
          captureQualityBand: String(inference.captureQualityBand || ""),
          failureReason: String(inference.failureReason || "")
        }
      }));

      appendFaceAuthAudit({
        id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        event: verified ? "FACE_AUTH_COMPLETE_VERIFIED" : "FACE_AUTH_COMPLETE_FAILED",
        sessionId,
        studentId: session.studentId,
        capturesCount: captures.length,
        challengeMeta,
        inference,
        createdAt: Date.now()
      });

      if (!inference.available) {
        return sendJson(response, 503, {
          success: false,
          verified: false,
          code: "INFERENCE_UNAVAILABLE",
          message: "Face verification servisi hazır deyil.",
          reason: inference.reason || "unknown",
          session: {
            id: sessionId,
            attempts: updatedSession?.attempts || 1,
            expiresAt: session.expiresAt
          }
        });
      }

      if (!verified) {
        const failureReason = String(inference.failureReason || "VERIFICATION_FAILED");
        const recommendedAction = String(inference.recommendedAction || "");
        return sendJson(response, 401, {
          success: false,
          verified: false,
          code: "FACE_VERIFICATION_FAILED",
          message: faceFailureMessage(failureReason),
          failureReason,
          recommendedAction,
          matchScore: Number(inference.faceSimilarity || 0),
          livenessScore: Number(inference.livenessScore || 0),
          antiSpoofLabel: String(inference.antiSpoofLabel || "unknown"),
          confidenceBand: String(inference.confidenceBand || "low"),
          captureQualityBand: String(inference.captureQualityBand || "poor"),
          retryable: Boolean(inference.retryable),
          debug: inference.debug || {},
          session: {
            id: sessionId,
            attempts: updatedSession?.attempts || 1,
            expiresAt: session.expiresAt
          }
        });
      }

      return sendJson(response, 200, {
        success: true,
        verified: true,
        message: "Üz doğrulaması uğurla tamamlandı.",
        matchScore: Number(inference.faceSimilarity || 0),
        livenessScore: Number(inference.livenessScore || 0),
        antiSpoofLabel: String(inference.antiSpoofLabel || "live"),
        confidenceBand: String(inference.confidenceBand || "high"),
        captureQualityBand: String(inference.captureQualityBand || "good"),
        retryable: false,
        student: student ? publicStudent(student) : null,
        session: {
          id: sessionId,
          attempts: updatedSession?.attempts || 1,
          expiresAt: session.expiresAt
        }
      });
    } catch (error) {
      appendFaceAuthAudit({
        id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        event: "FACE_AUTH_COMPLETE_ERROR",
        sessionId,
        studentId: session.studentId,
        capturesCount: captures.length,
        challengeMeta,
        error: String(error?.message || error),
        createdAt: Date.now()
      });
      return sendJson(response, 502, {
        success: false,
        verified: false,
        code: "FACE_VERIFICATION_ERROR",
        message: "Üz doğrulaması servisində xəta baş verdi."
      });
    }
  }

  if (request.method === "POST" && url.pathname === "/verify-card") {
    const payload = await readBody(request);
    const match = exactMatch(payload);
    if (match.status === "AMBIGUOUS") {
      return sendJson(response, 409, {
        verified: false,
        status: "AMBIGUOUS",
        cardNumber: match.cardNumber,
        message: "Bu iş nömrəsi datada birdən çox tələbəyə verilib. Yanlış profil açılmaması üçün avtomatik giriş dayandırıldı."
      });
    }
    if (match.status !== "VERIFIED") {
      return sendJson(response, 404, {
        verified: false,
        status: "NOT_FOUND",
        message: "Tələbə datasında bu vəsiqə nömrəsi tapılmadı."
      });
    }
    return sendJson(response, 200, {
      verified: true,
      status: "VERIFIED",
      confidence: 100,
      student: publicStudent(match.student)
    });
  }

  if (request.method === "POST" && url.pathname === "/login") {
    const payload = await readBody(request);
    const username = String(payload.username || "").trim();
    const password = String(payload.password || "").trim();
    const student = loginStudents.find(item => item.username === username && item.password === password);
    if (!student) {
      return sendJson(response, 401, {
        authenticated: false,
        message: "İstifadəçi adı və ya şifrə yanlışdır."
      });
    }
    return sendJson(response, 200, {
      authenticated: true,
      student: publicStudent(student)
    });
  }

  if (request.method === "POST" && url.pathname === "/ai-chat") {
    const payload = await readBody(request);
    const message = String(payload.message || "").trim();
    if (message.length < 2) {
      return sendJson(response, 400, {
        success: false,
        error: "Sual boş ola bilməz."
      });
    }
    if (message.length > 1200) {
      return sendJson(response, 400, {
        success: false,
        error: "Sual çox uzundur."
      });
    }

    const result = await askOpenAi(message);
    return sendJson(response, result.success ? 200 : 502, result);
  }

  sendJson(response, 404, { message: "Endpoint tapılmadı." });
});

server.on("error", error => {
  if (error.code === "EADDRINUSE") {
    console.error(`Port ${PORT} artıq istifadədədir. Backend artıq işləyirsə bu normaldır.`);
    console.error("Dayandırmaq üçün həmin terminalda Ctrl+C basın və ya başqa PORT seçin.");
    process.exit(1);
  }
  throw error;
});

server.listen(PORT, "0.0.0.0", () => {
  ensureRoom(officialRoom());
  console.log(`ATU Campus backend running on http://0.0.0.0:${PORT}`);
  console.log(`Loaded ${students.length} student records (${studentsByWorkNumber.size} unique work numbers)`);
  console.log(`Indexed ${photosByWorkNumber.size} student photos from ${studentPhotosDirectory}`);
});
