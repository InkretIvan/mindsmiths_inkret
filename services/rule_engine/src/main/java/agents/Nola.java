package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.telegramAdapter.TelegramAdapterAPI;
import lombok.*;
import com.mindsmiths.gpt3.GPT3AdapterAPI;
import com.mindsmiths.ruleEngine.util.Log;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Nola extends Agent {

    private String memory = "";

    public Nola() {
    }

    public Nola(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }


    public void sendMessage(String text) {
        String chatId = getConnections().get("telegram");
        TelegramAdapterAPI.sendMessage(chatId, text);
    }

    public void addMessageToMemory(String sender, String text){
        memory += String.format("%s: %s\n", sender, text);
    }

    public void askGPT3() {
        String intro = "This is a conversation between a human and an intelligent AI assistant named Nola.\n";
        simpleGPT3Request(intro + String.join("\n", memory) + "\nNola:");
    }

    public void simpleGPT3Request(String prompt) {
        Log.info("Prompt for GPT-3:\n" + prompt);
        GPT3AdapterAPI.complete(
            prompt, // input prompt
            "text-davinci-001", // model
            150, // max tokens
            0.9, // temperature
            1.0, // topP
            1, // N
            null, // logprobs
            false, // echo
            List.of("Human:", "Nola:"), // STOP words
            0.6, // presence penalty
            0.0, // frequency penalty
            1, // best of
            null // logit bias
        );
    }
}