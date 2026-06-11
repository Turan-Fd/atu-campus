$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$Python = "C:\Users\Admin\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe"
$Workbook = "C:\Users\Admin\Downloads\Statistika.xlsx"
$Photos = "C:\Users\Admin\Documents\student photos"
$Output = Join-Path $ProjectRoot "backend\data"

if (!(Test-Path $Python)) {
    throw "Bundled Python runtime tapılmadı."
}
if (!(Test-Path $Workbook)) {
    throw "Statistika.xlsx tapılmadı: $Workbook"
}
if (!(Test-Path $Photos)) {
    throw "student photos qovluğu tapılmadı: $Photos"
}

& $Python `
    (Join-Path $ProjectRoot "backend\scripts\import_student_data.py") `
    --workbook $Workbook `
    --photos $Photos `
    --output $Output
