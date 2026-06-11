import Foundation
import SwiftUI
import Vision
import UIKit

final class LocalProfileStore {
    private let key = "atu.campus.profile"

    func load() -> StudentProfile? {
        guard let data = UserDefaults.standard.data(forKey: key) else { return nil }
        return try? JSONDecoder().decode(StudentProfile.self, from: data)
    }

    func save(_ profile: StudentProfile) {
        guard let data = try? JSONEncoder().encode(profile) else { return }
        UserDefaults.standard.set(data, forKey: key)
    }

    func clear() {
        UserDefaults.standard.removeObject(forKey: key)
    }
}

final class BackendStudentService {
    var baseURL: URL = URL(string: "http://127.0.0.1:8080")!

    func verifyScannedProfile(_ profile: StudentProfile) async -> StudentProfile {
        let payload: [String: String] = [
            "name": cleanup(profile.name),
            "id": cleanup(profile.id),
            "group": cleanup(profile.group),
            "rawText": "\(profile.name) \(profile.id) \(profile.group)"
        ]

        guard
            let requestURL = URL(string: "/verify-card", relativeTo: baseURL),
            var request = try? jsonRequest(url: requestURL, body: payload)
        else { return profile }

        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            let decoded = try JSONDecoder().decode(VerifyResponse.self, from: data)
            guard decoded.verified, let student = decoded.student else { return profile }
            return StudentProfile(
                surname: student.surname ?? profile.surname,
                name: student.name ?? profile.name,
                fatherName: profile.fatherName,
                id: student.id ?? profile.id,
                faculty: student.faculty ?? profile.faculty,
                department: student.department ?? profile.department,
                specialty: student.specialty ?? profile.specialty,
                group: student.group ?? profile.group
            )
        } catch {
            return profile
        }
    }

    private func cleanup(_ value: String) -> String {
        value == "Oxunmadı" ? "" : value
    }

    private func jsonRequest(url: URL, body: [String: String]) throws -> URLRequest {
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.timeoutInterval = 8
        request.setValue("application/json; charset=utf-8", forHTTPHeaderField: "Content-Type")
        request.httpBody = try JSONSerialization.data(withJSONObject: body)
        return request
    }

    private struct VerifyResponse: Codable {
        let verified: Bool
        let student: VerifyStudent?
    }

    private struct VerifyStudent: Codable {
        let id: String?
        let name: String?
        let surname: String?
        let group: String?
        let faculty: String?
        let department: String?
        let specialty: String?
    }
}

final class AtuNewsService {
    private let newsURL = URL(string: "https://atu.edu.az/xeberler/-1")!

    func fetchNews() async -> [AtuNews] {
        do {
            let (data, _) = try await URLSession.shared.data(from: newsURL)
            guard let html = String(data: data, encoding: .utf8) else { return [] }
            return parseNews(html)
        } catch {
            return []
        }
    }

    private func parseNews(_ html: String) -> [AtuNews] {
        let pattern = #"<div class="blog-item.*?">.*?<img\s+src="([^"]+)".*?<span><i class="fa fa-calendar"></i>\s*<a href="#">([^<]+)</a>.*?<h3>\s*<a href="([^"]+)".*?>(.*?)</a></h3>.*?<div class="blog-desc">\s*<p>(.*?)</p>"#
        guard let regex = try? NSRegularExpression(pattern: pattern, options: [.dotMatchesLineSeparators, .caseInsensitive]) else { return [] }
        let range = NSRange(html.startIndex..<html.endIndex, in: html)
        return regex.matches(in: html, range: range).compactMap { match in
            guard match.numberOfRanges == 6 else { return nil }
            func group(_ idx: Int) -> String {
                guard let range = Range(match.range(at: idx), in: html) else { return "" }
                return String(html[range])
            }
            return AtuNews(
                title: clean(group(4)),
                date: clean(group(2)),
                summary: String(clean(group(5)).prefix(180)),
                url: clean(group(3)),
                imageUrl: clean(group(1))
            )
        }.filter { !$0.title.isEmpty }
    }

    private func clean(_ value: String) -> String {
        value
            .replacingOccurrences(of: "<[^>]+>", with: " ", options: .regularExpression)
            .replacingOccurrences(of: "&nbsp;", with: " ")
            .replacingOccurrences(of: "&amp;", with: "&")
            .replacingOccurrences(of: "&quot;", with: "\"")
            .replacingOccurrences(of: "&#039;", with: "'")
            .replacingOccurrences(of: "&lt;", with: "<")
            .replacingOccurrences(of: "&gt;", with: ">")
            .replacingOccurrences(of: "\\s+", with: " ", options: .regularExpression)
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }
}

final class AiChatService {
    var baseURLs: [URL] = [
        URL(string: "http://127.0.0.1:8080")!,
        URL(string: "http://192.168.31.158:8080")!,
        URL(string: "http://192.168.100.86:8080")!
    ]

    func sendMessage(_ text: String) async -> String {
        for base in baseURLs {
            do {
                let url = URL(string: "/ai-chat", relativeTo: base)!
                var request = URLRequest(url: url)
                request.httpMethod = "POST"
                request.timeoutInterval = 30
                request.setValue("application/json; charset=utf-8", forHTTPHeaderField: "Content-Type")
                request.httpBody = try JSONSerialization.data(withJSONObject: ["message": text])
                let (data, _) = try await URLSession.shared.data(for: request)
                let response = try JSONDecoder().decode(AIResponse.self, from: data)
                if let answer = response.answer, !answer.isEmpty { return answer }
                if let error = response.error, !error.isEmpty { return error }
            } catch {
                continue
            }
        }
        return "AI köməkçi backend-ə çata bilmədi."
    }

    private struct AIResponse: Codable {
        let success: Bool?
        let answer: String?
        let error: String?
    }
}

final class OCRService {
    func readStudentCard(front: UIImage?, back: UIImage?) async -> StudentProfile {
        let texts = await [front, back].asyncCompactMap { image in
            guard let image else { return nil }
            return await recognizeText(from: image)
        }.joined(separator: "\n")

        let id = extract(pattern: #"\b[0-9]{6,8}\b"#, in: texts) ?? "Oxunmadı"
        let group = extract(pattern: #"\b[0-9]{3,4}[a-zA-Z][0-9a-zA-Z]*\b"#, in: texts) ?? "Oxunmadı"
        let lines = texts.split(separator: "\n").map { String($0).trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
        let nameGuess = lines.first(where: { !$0.contains(id) && !$0.contains(group) }) ?? "Oxunmadı"

        return StudentProfile(
            surname: "",
            name: nameGuess,
            fatherName: "",
            id: id,
            faculty: "",
            department: "",
            specialty: "",
            group: group
        )
    }

    private func recognizeText(from image: UIImage) async -> String {
        guard let cgImage = image.cgImage else { return "" }
        return await withCheckedContinuation { continuation in
            let request = VNRecognizeTextRequest { request, _ in
                let text = (request.results as? [VNRecognizedTextObservation])?
                    .compactMap { $0.topCandidates(1).first?.string }
                    .joined(separator: "\n") ?? ""
                continuation.resume(returning: text)
            }
            request.recognitionLevel = .accurate
            request.usesLanguageCorrection = true
            let handler = VNImageRequestHandler(cgImage: cgImage)
            try? handler.perform([request])
        }
    }

    private func extract(pattern: String, in text: String) -> String? {
        text.range(of: pattern, options: .regularExpression).map { String(text[$0]) }
    }
}

extension Array {
    fileprivate func asyncCompactMap<T>(_ transform: @escaping (Element) async -> T?) async -> [T] {
        var values: [T] = []
        for element in self {
            if let value = await transform(element) {
                values.append(value)
            }
        }
        return values
    }
}
