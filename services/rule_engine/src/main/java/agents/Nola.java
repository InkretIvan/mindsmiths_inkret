package agents;

import com.mindsmiths.ruleEngine.model.Agent;
import com.mindsmiths.telegramAdapter.TelegramAdapterAPI;
import lombok.*;
import com.mindsmiths.gpt3.GPT3AdapterAPI;
import com.mindsmiths.ruleEngine.util.Log;
import java.util.Date;
import java.util.Arrays;
import com.mindsmiths.sdk.utils.Utils;
import models.Personality;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Nola extends Agent {

    private List<String> memory = new ArrayList<>();
    private Personality personality = Personality.friendlyAI;
    private Date lastInteractionTime;
    private boolean pinged;
    private int MAX_MEMORY = 6;

    public Nola() {
    }

    public Nola(String connectionName, String connectionId) {
        super(connectionName, connectionId);
    }

    public void changePersonality(){
        resetMemory();
        List<Personality> choices = new ArrayList<Personality>(
                Arrays.asList(Personality.values())
        );
        choices.remove(personality);
        //personality = Utils.randomChoice(choices);
        Random rand = new Random();
        int bound=choices.size();
        personality=choices.get(rand.nextInt(bound));

    }


    private void trimMemory() {
        if (memory.size() > MAX_MEMORY + 1)
            memory = memory.subList(memory.size() - 1 - MAX_MEMORY, memory.size());
    }
    
    public void sendMessage(String text) {
        String chatId = getConnections().get("telegram");
        TelegramAdapterAPI.sendMessage(chatId, text);
    }

    public void addMessageToMemory(String sender, String text) {
        memory.add(String.format("%s: %s\n", sender, text));
        trimMemory();
    }

    public void resetMemory() {
        memory.clear();
    }

    public void addInstruction(String text){
        memory.add(text + "\n");
        trimMemory();
    }

    public void askGPT3() {
        simpleGPT3Request(
            personality.getInstruction() + String.join("\n", memory) + personality.getAiName() + ":", personality.getTemp(),
            personality.getResponseLen(), List.of(personality.getAiName() + ":", personality.getHumanName() + ":")
        );
    }

    public void simpleGPT3Request(String prompt, Double temp, Integer responseLen, List<String> stop) {
        Log.info("Prompt for GPT-3:\n" + prompt);
        GPT3AdapterAPI.complete(
            prompt, // input prompt
            "text-davinci-001", // model
            responseLen, // max tokens
            temp, // temperature
            1.0, // topP
            1, // N
            null, // logprobs
            false, // echo
            stop, // STOP words
            0.6, // presence penalty
            0.0, // frequency penalty
            1, // best of
            null // logit bias
        );
    }
}