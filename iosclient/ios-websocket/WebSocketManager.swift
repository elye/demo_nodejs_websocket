import SwiftUI

class WebSocketManager: ObservableObject {
    @Published var messages: [String] = []
    
    private let socket: URLSessionWebSocketTask =
        URLSession(configuration: .default).webSocketTask(with: URL(string: "ws://localhost:8082")!)
    
    func setup() {
        socket.resume()
        self.receiveMessage()
    }
    
    func sendMessage(message: String) {
        self.socket.send(.string(message)) { (err) in
            if err != nil {
                print("Error on sending: \(err.debugDescription)")
            }
        }
        self.messages.append("Me: \(message)")

    }
    
    func receiveMessage() {
        socket.receive { result in
            switch result {
            case .failure(let error):
                print("Error Detected: \(error)")
            case .success(let message):
                switch message {
                case .string(let text):
                    DispatchQueue.main.async {
                        self.messages.append("You: \(text)")
                    }
                default:
                    print("Received data different format data")
                }
                self.receiveMessage()
            }
        }
    }
}
