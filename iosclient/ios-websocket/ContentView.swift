import SwiftUI

struct ContentView: View {
    @State var myMessage: String = ""
    @ObservedObject var wsManager = WebSocketManager()

    init() {
        wsManager.setup()
    }

    var body: some View {
        VStack {
            TextField("Message", text: $myMessage, onCommit: {
                guard !self.myMessage.isEmpty else { return }
                wsManager.sendMessage(message: self.myMessage)
                self.myMessage = ""
            })
            Divider()
            List(self.wsManager.messages, id: \.self) { message in
                Text(message)
            }
        }
        .padding(.vertical)
        .padding(.horizontal)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
