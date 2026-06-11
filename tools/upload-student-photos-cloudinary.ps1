$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$Python = "C:\Users\Admin\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe"
$Manifest = Join-Path $ProjectRoot "backend\data\student-photos.json"
$Photos = "C:\Users\Admin\Documents\student photos"

if (!(Test-Path $Python)) {
    throw "Bundled Python runtime tapılmadı."
}
if (!(Test-Path $Manifest)) {
    throw "student-photos.json tapılmadı: $Manifest"
}
if (!(Test-Path $Photos)) {
    throw "student photos qovluğu tapılmadı: $Photos"
}
if (!$env:CLOUDINARY_CLOUD_NAME -or !$env:CLOUDINARY_API_KEY -or !$env:CLOUDINARY_API_SECRET) {
    throw "CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY və CLOUDINARY_API_SECRET env-lərini əvvəlcə təyin et."
}

& $Python `
    (Join-Path $ProjectRoot "backend\scripts\upload_student_photos_cloudinary.py") `
    --manifest $Manifest `
    --photos $Photos `
    --overwrite
