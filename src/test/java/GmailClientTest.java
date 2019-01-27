import client.authenticator.EmailAuthenticator.Gmail;
import client.core.GmailClient;
import client.core.LoginRequiredClient;
import client.core.common.ReceivedMessage;
import client.core.common.SendedMessage;
import client.core.interfaces.IReceiver;
import client.core.interfaces.ISender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.mail.MessagingException;
import java.util.Set;
import java.util.stream.Collectors;

import static client.core.common.MessageUtil.profile;

@RunWith(JUnit4.class)
public class GmailClientTest {
    @Test
    public void tes() {
        final GmailClient client = getClient().auth();
        profile("send()", () -> client.send(buildMessage()));
    }

    @Test
    public void test() {
        final GmailClient client = getClient().auth();

        client.send(buildMessage(), new ISender.SendCallback() {
            @Override
            public void onError(MessagingException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onSuccess() {
                System.out.println("Sent");
            }
        });

        client.receive(new IReceiver.ReceiveCallback() {
            @Override
            public void onReceive(Set<ReceivedMessage> messages) {
                System.out.println("=====================================================");
                System.out.println("Received messages: " + messages
                        .stream()
                        .map(m -> (m.getMessage() + " => " + m.getDate()).trim())
                        .collect(Collectors.joining("\n"))
                );
                System.out.println("Received size: " + messages.size());
                System.out.println("=====================================================");
            }

            @Override
            public void onUpdate(ReceivedMessage message) {
                System.out.println("-----------------------------------------------------------------");
                System.out.println("New message: " + (message.getMessage() + " => " + message.getDate()).trim());
                System.out.println("-----------------------------------------------------------------");
            }

            @Override
            public void onError(MessagingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
    }

    private SendedMessage buildMessage() {
        return new SendedMessage("Yesterday", "All my troubles seemed so far away")
                .from("John Lennon")
                .to("serhiy.mazur0@gmail.com");
    }

    private GmailClient getClient() {
        return GmailClient.get()
                .loginWith(Gmail.auth("login", "password"))
                .beforeLogin(() -> System.out.println("Process login..."))
                .reconnectIfError(10000, 2)
                .onLoginError(e -> System.out.println("Login error: " + e.getMessage()))
                .onLoginSuccess(() -> System.out.println("Login successfully"));
    }
}