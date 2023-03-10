package ru.chursinov.meetingTelegramBot.bot.handler.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.chursinov.meetingTelegramBot.bot.BotCondition;
import ru.chursinov.meetingTelegramBot.bot.BotConditionUserContext;
import ru.chursinov.meetingTelegramBot.bot.UserDataCache;
import ru.chursinov.meetingTelegramBot.entity.UserProfileData;
import ru.chursinov.meetingTelegramBot.service.ReplyMessageService;

@Component
public class StartQuestionsMessageHandler implements MessageHandler {

    private final ReplyMessageService replyMessageService;
    private final BotConditionUserContext botConditionUserContext;

    private final UserDataCache userDataCache;

    @Autowired
    public StartQuestionsMessageHandler(ReplyMessageService replyMessageService, BotConditionUserContext botConditionUserContext, UserDataCache userDataCache) {
        this.replyMessageService = replyMessageService;
        this.botConditionUserContext = botConditionUserContext;
        this.userDataCache = userDataCache;
    }

    @Override
    public boolean canHandle(BotCondition botCondition) {
        return botCondition.equals(BotCondition.START_QUESTIONS);
    }

    @Override
    public SendMessage handle(Message message) {

        Long chatId = message.getChatId();

        if (message.getText().equals("Заполнить информацию о своей работе")) {
            return replyMessageService.getTextMessage(chatId, "Что было сделано вчера?");
        }

        BotCondition botCondition = BotCondition.TODAY;
        Long userId = message.getFrom().getId();

        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        userProfileData.setYesterday(message.getText());
        userDataCache.saveUserProfileData(userId, userProfileData);

        botConditionUserContext.setCurrentBotConditionForUserWithId(userId, botCondition);

        return replyMessageService.getTextMessage(chatId, "Какие рабочие планы на сегодня?");
    }
}
