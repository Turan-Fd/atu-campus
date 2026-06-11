import argparse
import hashlib
import json
import os
import time
import urllib.parse
import urllib.request
from pathlib import Path


def upload_photo(cloud_name: str, api_key: str, api_secret: str, photo_path: Path, public_id: str, overwrite: bool) -> str:
    timestamp = str(int(time.time()))
    params_to_sign = {
        "folder": "atu-campus/students",
        "overwrite": "true" if overwrite else "false",
        "public_id": public_id,
        "timestamp": timestamp,
    }
    signature_payload = "&".join(f"{key}={params_to_sign[key]}" for key in sorted(params_to_sign))
    signature = hashlib.sha1(f"{signature_payload}{api_secret}".encode("utf-8")).hexdigest()

    boundary = f"----ATUCampus{timestamp}"
    body = bytearray()

    def add_field(name: str, value: str):
        body.extend(f"--{boundary}\r\n".encode("utf-8"))
        body.extend(f'Content-Disposition: form-data; name="{name}"\r\n\r\n'.encode("utf-8"))
        body.extend(f"{value}\r\n".encode("utf-8"))

    for key, value in params_to_sign.items():
        add_field(key, value)
    add_field("api_key", api_key)
    add_field("signature", signature)

    file_name = photo_path.name
    mime_type = "image/jpeg"
    if photo_path.suffix.lower() == ".png":
        mime_type = "image/png"
    elif photo_path.suffix.lower() == ".webp":
        mime_type = "image/webp"

    body.extend(f"--{boundary}\r\n".encode("utf-8"))
    body.extend(
        f'Content-Disposition: form-data; name="file"; filename="{file_name}"\r\n'.encode("utf-8")
    )
    body.extend(f"Content-Type: {mime_type}\r\n\r\n".encode("utf-8"))
    body.extend(photo_path.read_bytes())
    body.extend(b"\r\n")
    body.extend(f"--{boundary}--\r\n".encode("utf-8"))

    request = urllib.request.Request(
        url=f"https://api.cloudinary.com/v1_1/{cloud_name}/image/upload",
        data=bytes(body),
        method="POST",
        headers={"Content-Type": f"multipart/form-data; boundary={boundary}"},
    )
    with urllib.request.urlopen(request, timeout=120) as response:
        payload = json.loads(response.read().decode("utf-8"))
    return payload["secure_url"]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", required=True, type=Path)
    parser.add_argument("--photos", required=True, type=Path)
    parser.add_argument("--cloud-name", default=os.environ.get("CLOUDINARY_CLOUD_NAME", ""))
    parser.add_argument("--api-key", default=os.environ.get("CLOUDINARY_API_KEY", ""))
    parser.add_argument("--api-secret", default=os.environ.get("CLOUDINARY_API_SECRET", ""))
    parser.add_argument("--limit", type=int, default=0)
    parser.add_argument("--overwrite", action="store_true")
    args = parser.parse_args()

    if not args.cloud_name or not args.api_key or not args.api_secret:
        raise SystemExit("Cloudinary məlumatları yoxdur. CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY və CLOUDINARY_API_SECRET verin.")

    manifest = json.loads(args.manifest.read_text(encoding="utf-8"))
    updated = dict(manifest)
    uploaded = 0
    skipped = 0
    failed = []

    for work_number, reference in manifest.items():
        if args.limit and uploaded >= args.limit:
            break
        if str(reference).startswith("http://") or str(reference).startswith("https://"):
            skipped += 1
            continue

        local_path = args.photos / str(reference)
        if not local_path.exists():
            failed.append({"workNumber": work_number, "reason": f"missing file: {local_path}"})
            continue

        public_id = urllib.parse.quote(str(work_number), safe="")
        try:
            secure_url = upload_photo(
                args.cloud_name,
                args.api_key,
                args.api_secret,
                local_path,
                public_id,
                args.overwrite,
            )
            updated[work_number] = secure_url
            uploaded += 1
            if uploaded % 50 == 0:
                args.manifest.write_text(json.dumps(updated, ensure_ascii=False, indent=2), encoding="utf-8")
                print(f"Uploaded {uploaded} photos...")
        except Exception as exc:
            failed.append({"workNumber": work_number, "reason": str(exc)})

    args.manifest.write_text(json.dumps(updated, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Uploaded: {uploaded}")
    print(f"Skipped existing URLs: {skipped}")
    print(f"Failed: {len(failed)}")
    if failed:
        error_path = args.manifest.parent / "student-photos-cloudinary-errors.json"
        error_path.write_text(json.dumps(failed, ensure_ascii=False, indent=2), encoding="utf-8")
        print(f"Errors written to: {error_path}")


if __name__ == "__main__":
    main()
