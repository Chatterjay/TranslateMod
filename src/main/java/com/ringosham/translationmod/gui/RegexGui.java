/*
 * Copyright (C) 2021 Ringosham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ringosham.translationmod.gui;

import com.google.common.primitives.Ints;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexGui extends CommonGui implements GuiYesNoCallback {
    //Regex must not be in conflict of the translated message. Otherwise the mod will stuck in a loop spamming the server.
    private static final String testMessage = "Notch --> English: Hello!";
    private static final int guiWidth = 400;
    private static final int guiHeight = 230;
    private static final List<String> cheatsheet;
    private static final List<List<String>> cheatsheetDesc;
    private static final String regexTest = "https://regexr.com";

    static {
        cheatsheet = new ArrayList<>();
        cheatsheetDesc = new ArrayList<>();
        for (int i = 0; i < 12; i++)
            cheatsheetDesc.add(new ArrayList<String>());
        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.dot"));
        cheatsheetDesc.get(0).add(I18n.format("translationmod.regex_gui.cheatsheet.dot.desc1"));
        cheatsheetDesc.get(0).add(I18n.format("translationmod.regex_gui.cheatsheet.dot.desc2"));
        cheatsheetDesc.get(0).add(I18n.format("translationmod.regex_gui.cheatsheet.dot.desc3"));

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.word"));
        cheatsheetDesc.get(1).add(I18n.format("translationmod.regex_gui.cheatsheet.word.desc1"));
        cheatsheetDesc.get(1).add(I18n.format("translationmod.regex_gui.cheatsheet.word.desc2"));

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.digit"));
        cheatsheetDesc.get(2).add(I18n.format("translationmod.regex_gui.cheatsheet.digit.desc1"));


        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.range"));
        cheatsheetDesc.get(3).add(I18n.format("translationmod.regex_gui.cheatsheet.range.desc1"));
        cheatsheetDesc.get(3).add(I18n.format("translationmod.regex_gui.cheatsheet.range.desc2"));
        cheatsheetDesc.get(3).add("\u2713 " + EnumChatFormatting.GREEN + "a"); // 示例字符无需国际化
        cheatsheetDesc.get(3).add("\u2713 " + EnumChatFormatting.GREEN + "b");
        cheatsheetDesc.get(3).add("\u2717 " + EnumChatFormatting.RED + "z");

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.star"));
        cheatsheetDesc.get(4).add(I18n.format("translationmod.regex_gui.cheatsheet.star.desc1"));
        cheatsheetDesc.get(4).add(I18n.format("translationmod.regex_gui.cheatsheet.star.desc2"));
        cheatsheetDesc.get(4).add("\u2713 " + EnumChatFormatting.GREEN + "N");
        cheatsheetDesc.get(4).add("\u2713 " + EnumChatFormatting.GREEN + "No");
        cheatsheetDesc.get(4).add("\u2713 " + EnumChatFormatting.GREEN + "Notch");

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.plus"));
        cheatsheetDesc.get(5).add(I18n.format("translationmod.regex_gui.cheatsheet.plus.desc1"));
        cheatsheetDesc.get(5).add(I18n.format("translationmod.regex_gui.cheatsheet.plus.desc2"));
        cheatsheetDesc.get(5).add("\u2717 " + EnumChatFormatting.RED + "N");
        cheatsheetDesc.get(5).add("\u2713 " + EnumChatFormatting.GREEN + "No");
        cheatsheetDesc.get(5).add("\u2713 " + EnumChatFormatting.GREEN + "Notch");

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.optional"));
        cheatsheetDesc.get(6).add(I18n.format("translationmod.regex_gui.cheatsheet.optional.desc1"));
        cheatsheetDesc.get(6).add(I18n.format("translationmod.regex_gui.cheatsheet.optional.desc2"));
        cheatsheetDesc.get(6).add("\u2713 " + EnumChatFormatting.GREEN + "VIP PlayerName");
        cheatsheetDesc.get(6).add("\u2713 " + EnumChatFormatting.GREEN + "PlayerName");

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.brace"));
        cheatsheetDesc.get(7).add(I18n.format("translationmod.regex_gui.cheatsheet.brace.desc1"));
        cheatsheetDesc.get(7).add(I18n.format("translationmod.regex_gui.cheatsheet.brace.desc2"));
        cheatsheetDesc.get(7).add(I18n.format("translationmod.regex_gui.cheatsheet.brace.desc3"));
        cheatsheetDesc.get(7).add(I18n.format("translationmod.regex_gui.cheatsheet.brace.desc4"));
        cheatsheetDesc.get(7).add("\u2713 " + EnumChatFormatting.GREEN + "Level 1");
        cheatsheetDesc.get(7).add("\u2713 " + EnumChatFormatting.GREEN + "Level 420");
        cheatsheetDesc.get(7).add("\u2717 " + EnumChatFormatting.RED + "Level 42069");

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.either"));
        cheatsheetDesc.get(8).add(I18n.format("translationmod.regex_gui.cheatsheet.either.desc1"));
        cheatsheetDesc.get(8).add(I18n.format("translationmod.regex_gui.cheatsheet.either.desc2"));
        cheatsheetDesc.get(8).add("\u2713 " + EnumChatFormatting.GREEN + "Dead PlayerName");
        cheatsheetDesc.get(8).add("\u2713 " + EnumChatFormatting.GREEN + "Alive PlayerName");
        cheatsheetDesc.get(8).add("\u2717 " + EnumChatFormatting.RED + "DeadAlive PlayerName");

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.group"));
        cheatsheetDesc.get(9).add(I18n.format("translationmod.regex_gui.cheatsheet.group.desc1"));
        cheatsheetDesc.get(9).add(I18n.format("translationmod.regex_gui.cheatsheet.group.desc2"));
        cheatsheetDesc.get(9).add(I18n.format("translationmod.regex_gui.cheatsheet.group.desc3"));
        cheatsheetDesc.get(9).add(I18n.format("translationmod.regex_gui.cheatsheet.group.desc4"));

        cheatsheet.add(I18n.format("translationmod.regex_gui.cheatsheet.escape"));
        cheatsheetDesc.get(10).add(I18n.format("translationmod.regex_gui.cheatsheet.escape.desc1"));
        cheatsheetDesc.get(10).add(I18n.format("translationmod.regex_gui.cheatsheet.escape.desc2"));
        cheatsheetDesc.get(10).add(I18n.format("translationmod.regex_gui.cheatsheet.escape.correct") + EnumChatFormatting.GREEN + " \\(VIP\\) \\w+");
        cheatsheetDesc.get(10).add(I18n.format("translationmod.regex_gui.cheatsheet.escape.wrong") + EnumChatFormatting.RED + " (VIP) \\w+");
    }

    private int index;
    private final LinkedList<String> regexes = new LinkedList<>();
    private final LinkedList<Integer> groups = new LinkedList<>();
    private GuiTextField regexTextbox;
    private GuiTextField groupTextBox;

    {
        regexes.addAll(ConfigManager.INSTANCE.getRegexList());
        groups.addAll(ConfigManager.INSTANCE.getGroupList());
        index = regexes.size() - 1;
    }

    RegexGui() {
        super(guiHeight, guiWidth);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        fontRendererObj.drawString(I18n.format("translationmod.regex_gui.title"), getLeftMargin(), getTopMargin(), 0x555555);
        fontRendererObj.drawString(I18n.format("translationmod.regex_gui.desc1"), getLeftMargin(), getYOrigin() + 15, 0x555555);
        fontRendererObj.drawString(I18n.format("translationmod.regex_gui.desc2"), getLeftMargin(), getYOrigin() + 25, 0x555555);
        fontRendererObj.drawString(I18n.format("translationmod.regex_gui.cheatsheet.title"), getLeftMargin(), getYOrigin() + 35, 0x555555);
        fontRendererObj.drawString(I18n.format("translationmod.regex_gui.tip"), getLeftMargin(), getYOrigin() + guiHeight - 40, 0x555555);
        fontRendererObj.drawString((index + 1) + I18n.format("translationmod.regex_gui.pagination.of") + Math.max(index + 1, regexes.size()),
                getLeftMargin() + 15 + smallButtonLength * 2, getYOrigin() + guiHeight - regularButtonHeight, 0x555555);
        String regex = regexTextbox.getText();
        int group = groupTextBox.getText().isEmpty() ? -1 : Integer.parseInt(groupTextBox.getText());
        if (validateRegex(regex)) {
            if (!isRegexConflict(regex)) {
                int groupCount = countGroups(regex);
                if (groupCount == 0)
                    fontRendererObj.drawString(EnumChatFormatting.YELLOW + I18n.format("translationmod.regex_gui.validate.no_group"),
                            getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                else
                    fontRendererObj.drawString(EnumChatFormatting.GREEN + I18n.format("translationmod.regex_gui.validate.valid"),
                            getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);

                fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.possible_match") + findMatch(getChatLog(), regex),
                        getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                if (groupCount > 0)
                    fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.group_number") + "(1 - " + groupCount + ")",
                            getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
                else
                    fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.group_number") + "(?)",
                            getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);

                fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.matching_username") + matchUsername(findMatch(getChatLog(), regex), regex, group),
                        getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            } else {
                fontRendererObj.drawString(EnumChatFormatting.RED + I18n.format("translationmod.regex_gui.validate.conflict"),
                        getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
                fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.possible_match") + "---",
                        getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
                fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.matching_username") + "---",
                        getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
                fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.group_number") + "(?)",
                        getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
            }
        } else {
            fontRendererObj.drawString(EnumChatFormatting.RED + I18n.format("translationmod.regex_gui.validate.invalid"),
                    getLeftMargin(), getYOrigin() + guiHeight - 120, 0x555555);
            fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.possible_match") + "---",
                    getLeftMargin(), getYOrigin() + guiHeight - 110, 0x555555);
            fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.matching_username") + "---",
                    getLeftMargin(), getYOrigin() + guiHeight - 70, 0x555555);
            fontRendererObj.drawString(I18n.format("translationmod.regex_gui.validate.group_number") + "(?)",
                    getLeftMargin(), getYOrigin() + guiHeight - 80, 0x555555);
        }
        regexTextbox.drawTextBox();
        groupTextBox.drawTextBox();
        //Draw tooltips
        for (int i = 5; i < this.buttonList.size(); i++) {
            HoveringText button = (HoveringText) this.buttonList.get(i);
            if (button.isMouseOver())
                drawHoveringText(button.getHoverText(), x, y);
        }
    }

    @Override
    public void initGui() {
        regexTextbox = new GuiTextField(0, this.fontRendererObj, getLeftMargin(), getYOrigin() + guiHeight - 100, guiWidth - 10, 15);
        regexTextbox.setCanLoseFocus(true);
        regexTextbox.setMaxStringLength(200);
        regexTextbox.setEnableBackgroundDrawing(true);
        regexTextbox.setText(regexes.get(index));
        regexTextbox.setFocused(true);
        groupTextBox = new GuiTextField(1, this.fontRendererObj, getLeftMargin(), getYOrigin() + guiHeight - 60, guiWidth - 10, 15);
        groupTextBox.setCanLoseFocus(true);
        groupTextBox.setMaxStringLength(10);
        groupTextBox.setEnableBackgroundDrawing(true);
        groupTextBox.setText(Integer.toString(groups.get(index)));
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new TextButton(0, getRightMargin(150), getYOrigin() + 25, getTextWidth(regexTest), regexTest, 0x0000aa));
        this.buttonList.add(new GuiButton(1, getLeftMargin() + 5 + smallButtonLength, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, I18n.format("translationmod.regex_gui.button.add")));
        this.buttonList.add(new GuiButton(2, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, I18n.format("translationmod.regex_gui.button.save_close")));
        this.buttonList.add(new GuiButton(3, getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength, I18n.format("translationmod.regex_gui.button.prev")));
        this.buttonList.add(new GuiButton(4, getRightMargin(regularButtonWidth) - 5 - regularButtonWidth, getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, I18n.format("translationmod.regex_gui.button.reset_default")));

        //Needs to be cleared since resizing the window calls initGui() again
        this.buttonList.add(new HoveringText(5, getLeftMargin(), getYOrigin() + 45, cheatsheet.get(0), cheatsheetDesc.get(0)));
        this.buttonList.add(new HoveringText(6, getLeftMargin(), getYOrigin() + 55, cheatsheet.get(1), cheatsheetDesc.get(1)));
        this.buttonList.add(new HoveringText(7, getLeftMargin(), getYOrigin() + 65, cheatsheet.get(2), cheatsheetDesc.get(2)));
        this.buttonList.add(new HoveringText(8, getLeftMargin(), getYOrigin() + 75, cheatsheet.get(3), cheatsheetDesc.get(3)));
        this.buttonList.add(new HoveringText(9, getLeftMargin(), getYOrigin() + 85, cheatsheet.get(4), cheatsheetDesc.get(4)));
        this.buttonList.add(new HoveringText(10, getLeftMargin(), getYOrigin() + 95, cheatsheet.get(5), cheatsheetDesc.get(5)));
        this.buttonList.add(new HoveringText(11, getLeftMargin() + 210, getYOrigin() + 45, cheatsheet.get(6), cheatsheetDesc.get(6)));
        this.buttonList.add(new HoveringText(12, getLeftMargin() + 210, getYOrigin() + 55, cheatsheet.get(7), cheatsheetDesc.get(7)));
        this.buttonList.add(new HoveringText(13, getLeftMargin() + 210, getYOrigin() + 65, cheatsheet.get(8), cheatsheetDesc.get(8)));
        this.buttonList.add(new HoveringText(14, getLeftMargin() + 210, getYOrigin() + 75, cheatsheet.get(9), cheatsheetDesc.get(9)));
        this.buttonList.add(new HoveringText(15, getLeftMargin() + 210, getYOrigin() + 85, cheatsheet.get(10), cheatsheetDesc.get(10)));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        Keyboard.enableRepeatEvents(false);
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiConfirmOpenLink(this, regexTest, 0, false));
                break;
            case 1:
                //Add/next regex
                regexes.set(index, regexTextbox.getText());
                if (groupTextBox.getText().trim().isEmpty())
                    groups.set(index, 0);
                else
                    groups.set(index, Integer.parseInt(groupTextBox.getText()));
                index++;
                if (index == regexes.size()) {
                    button.enabled = false;
                    regexes.add("");
                    groups.add(1);
                    regexTextbox.setText("");
                    groupTextBox.setText("1");
                } else {
                    regexTextbox.setText(regexes.get(index));
                    groupTextBox.setText(groups.get(index).toString());
                    button.displayString = ">";
                    button.enabled = true;
                }
                if (index >= regexes.size() - 1)
                    button.displayString = "+";
                this.buttonList.get(3).enabled = true;
                regexTextbox.setFocused(true);
                regexTextbox.setCursorPositionEnd();
                Keyboard.enableRepeatEvents(true);
                break;
            case 2:
                //Save and close
                regexes.set(index, regexTextbox.getText());
                if (groupTextBox.getText().trim().isEmpty())
                    groups.set(index, 0);
                else
                    groups.set(index, Integer.parseInt(groupTextBox.getText()));
                applySettings();
                mc.displayGuiScreen(null);
                break;
            case 3:
                //Previous regex
                //Discard changes if the textboxes are empty.
                if (regexTextbox.getText().trim().isEmpty() || groupTextBox.getText().isEmpty()) {
                    regexes.remove(index);
                    groups.remove(index);
                } else {
                    regexes.set(index, regexTextbox.getText());
                    groups.set(index, Integer.parseInt(groupTextBox.getText()));
                }
                index--;
                if (index == 0)
                    button.enabled = false;
                if (regexes.size() - 1 == index)
                    this.buttonList.get(1).displayString = "+";
                else
                    this.buttonList.get(1).displayString = ">";
                this.buttonList.get(1).enabled = true;
                regexTextbox.setText(regexes.get(index));
                groupTextBox.setText(groups.get(index).toString());
                regexTextbox.setFocused(true);
                regexTextbox.setCursorPositionEnd();
                Keyboard.enableRepeatEvents(true);
                break;
            case 4:
                this.buttonList.get(3).enabled = true;
                this.buttonList.get(1).displayString = "+";
                regexes.clear();
                regexes.addAll(Arrays.asList(ConfigManager.defaultRegex));
                groups.clear();
                groups.addAll(Ints.asList(ConfigManager.defaultGroups));
                index = regexes.size() - 1;
                regexTextbox.setText(regexes.get(index));
                groupTextBox.setText(groups.get(index).toString());
                regexTextbox.setFocused(true);
                regexTextbox.setCursorPositionEnd();
                Keyboard.enableRepeatEvents(true);
                break;
        }
    }

    @Override
    public void keyTyped(char typedchar, int keycode) throws IOException {
        this.regexTextbox.textboxKeyTyped(typedchar, keycode);
        if (this.groupTextBox.isFocused()) {
            if ((typedchar >= 48 && typedchar <= 57) || typedchar == 8)
                //No group 0 allowed.
                if (this.groupTextBox.getText().isEmpty() && typedchar != 48)
                    this.groupTextBox.textboxKeyTyped(typedchar, keycode);
                else if (!this.groupTextBox.getText().isEmpty())
                    this.groupTextBox.textboxKeyTyped(typedchar, keycode);
        }
        if (keycode == Keyboard.KEY_E && !this.regexTextbox.isFocused())
            mc.displayGuiScreen(null);
        else
            super.keyTyped(typedchar, keycode);
    }

    @Override
    public void mouseClicked(int x, int y, int state) throws IOException {
        super.mouseClicked(x, y, state);
        this.regexTextbox.mouseClicked(x, y, state);
        this.groupTextBox.mouseClicked(x, y, state);
    }

    @Override
    public void confirmClicked(boolean userClicked, int userResponse) {
        if (userResponse == 0) {
            if (userClicked)
                openLink();
            mc.displayGuiScreen(this);
        }
    }

    private void openLink() {
        if (!Desktop.isDesktopSupported()) {
            Log.logger.error(I18n.format("translationmod.regex_gui.error.cannot_open_link"));
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(regexTest));
        } catch (IOException | URISyntaxException e) {
            Log.logger.error(I18n.format("translationmod.regex_gui.error.cannot_open_link"));
        }
    }

    private boolean validateRegex(String regex) {
        if (regex == null)
            return false;
        if (regex.trim().isEmpty())
            return false;
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            return false;
        }
        return true;
    }

    private int countGroups(String regex) {
        Pattern pattern = Pattern.compile(regex);
        //Why is matching even needed... This is stupid.
        Matcher matcher = pattern.matcher(I18n.format("translationmod.regex_gui.count_groups.test_string"));
        return matcher.groupCount();
    }

    //Ensure the regex does not conflict with the translated chat output.
    private boolean isRegexConflict(String regex) {
        if (!regex.contains("^"))
            regex = "^" + regex;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testMessage);
        return matcher.find();
    }

    //Gets the chat log of 20 messages for testing regex
    private List<String> getChatLog() {
        //Chat log is a private field.
        List<ChatLine> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), "field_146252_h");
        //For 1.7.10 debug use.
        //List<ChatLine> fullChatLog = ObfuscationReflectionHelper.getPrivateValue(GuiNewChat.class, Minecraft.getMinecraft().ingameGUI.getChatGUI(), "chatLines");
        List<String> chatLog = new ArrayList<>();
        for (int i = 0; i < Math.min(fullChatLog.size(), 20); i++)
            chatLog.add(fullChatLog.get(i).getChatComponent().getUnformattedText().replaceAll("§(.)", ""));
        return chatLog;
    }

    //An indicator to see how much the regex matches the chat message
    private String findMatch(List<String> chatLog, String regex) {
        if (!regex.contains("^"))
            regex = "^" + regex;
        Pattern pattern = Pattern.compile(regex);
        for (String message : chatLog) {
            Matcher matcher = pattern.matcher(message);
            if (!matcher.find())
                continue;
            String matchMessage = EnumChatFormatting.GREEN + matcher.group(0) + EnumChatFormatting.DARK_GRAY + message.replace(matcher.group(0), "");
            String shorten = matchMessage;
            for (int i = getTextWidth(matchMessage); i > 120; i--) {
                shorten = shorten.substring(0, matchMessage.length() - 1);
            }
            shorten = shorten + "...";
            return matchMessage.length() < shorten.length() ? matchMessage : shorten;
        }
        return EnumChatFormatting.RED + I18n.format("translationmod.regex_gui.match.no_match");
    }

    private String matchUsername(String message, String regex, int group) {
        if (group == -1 || group > countGroups(regex) ||  message.equals(EnumChatFormatting.RED + I18n.format("translationmod.regex_gui.match.no_username")))
            return "---";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find())
            return EnumChatFormatting.RED + I18n.format("translationmod.regex_gui.match.no_username");
        return matcher.group(group);
    }

    private void applySettings() {
        for (int i = 0; i < regexes.size(); i++) {
            if (!validateRegex(regexes.get(i)) || isRegexConflict(regexes.get(i))) {
                regexes.remove(i);
                groups.remove(i);
                i--;
                continue;
            }
            int groupCount = countGroups(regexes.get(i));
            if (groupCount < groups.get(i)) {
                regexes.remove(i);
                groups.remove(i);
                i--;
            }
        }
        ConfigManager.INSTANCE.setRegexList(regexes);
        ConfigManager.INSTANCE.setGroupList(groups);
        //Let the manager do all the validation
        ConfigManager.INSTANCE.syncConfig();
        ChatUtil.printChatMessage(true, I18n.format("translationmod.regex_gui.chat.regex_applied"), EnumChatFormatting.WHITE);
    }

    //Must be inner class due to protected access to drawHoveringText in GuiScreen
    public class HoveringText extends GuiButton {
        private final List<String> hoverText;

        public HoveringText(int buttonId, int x, int y, String buttonText, List<String> hoverText) {
            super(buttonId, x, y, buttonText);
            this.hoverText = hoverText;
            this.height = 10;
            this.width = getTextWidth(buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            //Do not set glColor in 1.8.9
            //GL11.glColor4f(1, 1, 1, 1);
            mc.fontRendererObj.drawString(this.displayString, xPosition, yPosition, 0xff555555, false);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        }

        List<String> getHoverText() {
            return hoverText;
        }
    }
}
