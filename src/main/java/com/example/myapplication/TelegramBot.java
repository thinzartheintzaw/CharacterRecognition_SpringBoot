package com.example.myapplication;

import com.example.myapplication.service.TesseractOCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final TesseractOCRService tesseractOCRService;

    @Autowired
    public TelegramBot(TesseractOCRService tesseractOCRService) {
        super("6987615814:AAG1jEYPzDKdlQlnuZL4TumjtW4YJE221x0");
        this.tesseractOCRService = tesseractOCRService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                String text = message.getText();
                if (text.equals("/start")) {
                    sendStartMessage(message.getChatId());
                }
                else if (text.equals("/instructions")) {
                    sendInstructionsMessage(message.getChatId());
                }
            }

            // Check if the update has a message
            if (update.hasMessage() && update.getMessage().hasPhoto()) {
                long chatId = update.getMessage().getChatId();
                String language = update.getMessage().getCaption();
                List<PhotoSize> photos = update.getMessage().getPhoto();

                // Get the last photo (which is the largest size) or throw an exception if no photo is found
                Optional<PhotoSize> largestPhoto = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst();

                String fileId;
                if (largestPhoto.isPresent()) {
                    fileId = largestPhoto.get().getFileId();

                    // Use the Telegram Bot API to download the photo file content
                    GetFile getFileMethod = new GetFile();
                    getFileMethod.setFileId(fileId);
                    org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);

                    // Download file content as a File object
                    java.io.File downloadedFile = downloadFile(file.getFilePath());

                    // Convert the File object to an input stream
                    InputStream photoStream = null;
                    try {
                        photoStream = new FileInputStream(downloadedFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        // Handle the case where the file is not found
                    }


                    // Use OCR service to recognize text from the image
                    String recognizedText = tesseractOCRService.recognizeText(photoStream,language);

                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("OCR Result: " + recognizedText);
                    execute(message);
                } else {
                    // Handle case where no photos are present in the message
                    // For example, you could send a message to the user indicating that no photo was found
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("No photo found in the message.");
                    execute(message);
                }
            }
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }


    }
    private void sendInstructionsMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("To use EasyOCR, you can follow these steps:\n" +
                "1. Upload a photo containing text\n" +
                "2. Type the languages you want to convert (e.g., eng, mya, hindi)");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendStartMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hello there! Welcome to EasyOCR. CLICK \"Instructions\" to help you for details.");

        // Create keyboard markup
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Create a list of keyboard rows
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();

        // Create a button
        KeyboardButton button = new KeyboardButton();
        button.setText("/instructions");

        // Add button to row
        row.add(button);

        // Add row to keyboard
        keyboard.add(row);

        // Set the keyboard
        replyKeyboardMarkup.setKeyboard(keyboard);

        // Set the reply keyboard to the message
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Easyocr1234_bot";
    }

    @Override
    public String getBotToken() {
        return "6987615814:AAG1jEYPzDKdlQlnuZL4TumjtW4YJE221x0";
    }
}
