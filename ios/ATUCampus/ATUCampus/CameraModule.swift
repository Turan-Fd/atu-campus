import SwiftUI
import AVFoundation
import UIKit

final class CameraViewModel: NSObject, ObservableObject, AVCapturePhotoCaptureDelegate {
    @Published var session = AVCaptureSession()
    @Published var isConfigured = false
    @Published var currentSide: ScanSide = .front
    @Published var frontImage: UIImage?
    @Published var backImage: UIImage?
    @Published var cameraError: String?

    private let photoOutput = AVCapturePhotoOutput()
    private var completion: ((UIImage?) -> Void)?

    func requestAndConfigure() async -> Bool {
        let status = AVCaptureDevice.authorizationStatus(for: .video)
        if status == .authorized {
            configureSession()
            return true
        }
        if status == .notDetermined {
            let granted = await withCheckedContinuation { continuation in
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    continuation.resume(returning: granted)
                }
            }
            if granted { configureSession() }
            return granted
        }
        return false
    }

    private func configureSession() {
        guard !isConfigured else { return }
        session.beginConfiguration()
        session.sessionPreset = .photo
        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back),
              let input = try? AVCaptureDeviceInput(device: device)
        else {
            cameraError = "Kamera hazır deyil."
            session.commitConfiguration()
            return
        }
        if session.canAddInput(input) { session.addInput(input) }
        if session.canAddOutput(photoOutput) { session.addOutput(photoOutput) }
        session.commitConfiguration()
        isConfigured = true
        DispatchQueue.global(qos: .userInitiated).async { self.session.startRunning() }
    }

    func capture(onComplete: @escaping (Bool) -> Void) {
        completion = { [weak self] image in
            guard let self else { return }
            if self.currentSide == .front {
                self.frontImage = image
                self.currentSide = .back
                onComplete(false)
            } else {
                self.backImage = image
                onComplete(true)
            }
        }
        photoOutput.capturePhoto(with: AVCapturePhotoSettings(), delegate: self)
    }

    func reset() {
        frontImage = nil
        backImage = nil
        currentSide = .front
        cameraError = nil
    }

    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        if let error {
            cameraError = "Şəkil çəkilə bilmədi: \(error.localizedDescription)"
            completion?(nil)
            return
        }
        let image = photo.fileDataRepresentation().flatMap(UIImage.init(data:))
        completion?(image)
    }
}

struct CameraPreviewView: UIViewRepresentable {
    @ObservedObject var model: CameraViewModel

    func makeUIView(context: Context) -> PreviewView {
        let view = PreviewView()
        view.previewLayer.session = model.session
        view.previewLayer.videoGravity = .resizeAspectFill
        return view
    }

    func updateUIView(_ uiView: PreviewView, context: Context) {
        uiView.previewLayer.session = model.session
    }
}

final class PreviewView: UIView {
    override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }
    var previewLayer: AVCaptureVideoPreviewLayer { layer as! AVCaptureVideoPreviewLayer }
}
