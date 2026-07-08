Place the MediaPipe Face Landmarker task model in this directory with the exact file name:

face_landmarker.task

Current Android helper:
- app/src/main/java/com/atu/campus/services/FaceChallengeAnalyzer.kt

Expected runtime behavior:
- if the file exists, live challenge analysis becomes active
- if the file is missing, the face verification screen stays in fallback mode

Recommended next step:
- add the official MediaPipe face landmarker model bundle here
- then test blink and left-turn detection on a real Android device
