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

import com.ringosham.translationmod.client.LangManager;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
public class ConfigGui extends CommonGui {
    private static final int guiWidth = 250;
    private static final int guiHeight = 206;
    private static final String targetTooltip = "The language your chat will be translated to";
    private static final List<String> selfTooltip = new ArrayList<>();
    private static final List<String> speakAsTooltip = new ArrayList<>();
    private static final List<String> regexTooltip = new ArrayList<>();
    private static final List<String> apiKeyTooltip = new ArrayList<>();
    private static final List<String> colorTooltip = new ArrayList<>();
    private static final List<String> boldTooltip = new ArrayList<>();
    private static final List<String> underlineTooltip = new ArrayList<>();
    private static final List<String> italicTooltip = new ArrayList<>();
    private static final List<String> signTooltip = new ArrayList<>();
    //If this instance is between transition between other GUIs
    private boolean isTransition = false;
    private Language targetLang;
    private Language speakAsLang;
    private Language selfLang;
    private String color;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean translateSign;

    private List<String> getTargetTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.target_language"));
        return tooltip;
    }

    // 自身语言Tooltip（多行）
    private List<String> getSelfTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.self_language.line1"));
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.self_language.line2"));
        return tooltip;
    }

    // 发言模拟语言Tooltip（多行）
    private List<String> getSpeakAsTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.speak_as_language.line1"));
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.speak_as_language.line2"));
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.speak_as_language.line3"));
        return tooltip;
    }

    // 正则列表Tooltip（多行）
    private List<String> getRegexTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.regex_list.line1"));
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.regex_list.line2"));
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.regex_list.line3"));
        return tooltip;
    }

    // API密钥Tooltip（单行）
    private List<String> getApiKeyTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.api_key"));
        return tooltip;
    }

    // 消息颜色Tooltip（单行）
    private List<String> getColorTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.color"));
        return tooltip;
    }

    // 粗体Tooltip（单行）
    private List<String> getBoldTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.bold"));
        return tooltip;
    }

    // 斜体Tooltip（单行）
    private List<String> getItalicTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.italic"));
        return tooltip;
    }

    // 下划线Tooltip（单行）
    private List<String> getUnderlineTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.underline"));
        return tooltip;
    }

    // 翻译牌子Tooltip（单行）
    private List<String> getSignTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(I18n.format("translationmod.gui.settings.tooltip.sign"));
        return tooltip;
    }

    ConfigGui() {
        super(guiHeight, guiWidth);
    }

    //Use for passing unsaved configurations between GUIs
    ConfigGui(ConfigGui instance, int langSelect, Language lang) {
        super(guiHeight, guiWidth);
        this.targetLang = instance.targetLang;
        this.speakAsLang = instance.speakAsLang;
        this.selfLang = instance.selfLang;
        this.color = instance.color;
        this.bold = instance.bold;
        this.italic = instance.italic;
        this.underline = instance.underline;
        this.translateSign = instance.translateSign;
        this.isTransition = true;
        if (lang != null) {
            switch (langSelect) {
                case 0:
                    this.targetLang = lang;
                    break;
                case 1:
                    this.selfLang = lang;
                    break;
                case 2:
                    this.speakAsLang = lang;
                    break;
            }
        }
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        String settingsTitle = I18n.format("translationmod.gui.settings.title");
        // 各类标签文本
        String regexListLabel = I18n.format("translationmod.gui.settings.label.regex_list");
        String targetLangLabel = I18n.format("translationmod.gui.settings.label.target_language");
        String selfLangLabel = I18n.format("translationmod.gui.settings.label.self_language");
        String speakAsLangLabel = I18n.format("translationmod.gui.settings.label.speak_as_language");
        String previewLabel = I18n.format("translationmod.gui.settings.label.preview");
        String previewText = I18n.format("translationmod.gui.settings.preview.text", "Notch", "English", "Hello!");

        fontRendererObj.drawString(settingsTitle, getLeftMargin(), getYOrigin() + 5, 0x555555);
        fontRendererObj.drawString(regexListLabel, getLeftMargin(), getYOrigin() + 25, 0x555555);
        fontRendererObj.drawString(targetLangLabel, getLeftMargin(), getYOrigin() + 55, 0x555555);
        fontRendererObj.drawString(selfLangLabel, getLeftMargin(), getYOrigin() + 75, 0x555555);
        fontRendererObj.drawString(speakAsLangLabel, getLeftMargin(), getYOrigin() + 95, 0x555555);
        fontRendererObj.drawString(previewLabel, getLeftMargin(), getYOrigin() + 115, 0x555555);
        StringBuilder builder = new StringBuilder();
        builder.append(EnumChatFormatting.getValueByName(color));
        if (bold)
            builder.append(EnumChatFormatting.BOLD);
        if (italic)
            builder.append(EnumChatFormatting.ITALIC);
        if (underline)
            builder.append(EnumChatFormatting.UNDERLINE);
        fontRendererObj.drawString(builder + previewText, getLeftMargin() + 45, getYOrigin() + 115, 0);
        if (this.buttonList.get(2).isMouseOver()) {
            drawHoveringText(getTargetTooltip(), x, y);
        }

        // 自身语言Tooltip
        if (this.buttonList.get(3).isMouseOver()) {
            drawHoveringText(getSelfTooltip(), x, y);
        }

        // 发言模拟语言Tooltip
        if (this.buttonList.get(4).isMouseOver()) {
            drawHoveringText(getSpeakAsTooltip(), x, y);
        }

        // 正则列表Tooltip
        if (this.buttonList.get(11).isMouseOver()) {
            drawHoveringText(getRegexTooltip(), x, y);
        }

        // API密钥Tooltip
        if (this.buttonList.get(6).isMouseOver()) {
            drawHoveringText(getApiKeyTooltip(), x, y);
        }

        // 翻译牌子Tooltip
        if (this.buttonList.get(5).isMouseOver()) {
            drawHoveringText(getSignTooltip(), x, y);
        }

        // 消息颜色Tooltip
        if (this.buttonList.get(7).isMouseOver()) {
            drawHoveringText(getColorTooltip(), x, y);
        }

        // 粗体Tooltip
        if (this.buttonList.get(8).isMouseOver()) {
            drawHoveringText(getBoldTooltip(), x, y);
        }

        // 斜体Tooltip
        if (this.buttonList.get(9).isMouseOver()) {
            drawHoveringText(getItalicTooltip(), x, y);
        }

        // 下划线Tooltip
        if (this.buttonList.get(10).isMouseOver()) {
            drawHoveringText(getUnderlineTooltip(), x, y);
        }
    }

    @Override
    public void initGui() {
        if (!isTransition) {
            color = ConfigManager.INSTANCE.getColor();
            bold = ConfigManager.INSTANCE.isBold();
            italic = ConfigManager.INSTANCE.isItalic();
            underline = ConfigManager.INSTANCE.isUnderline();
            translateSign = ConfigManager.INSTANCE.isTranslateSign();
            targetLang = ConfigManager.INSTANCE.getTargetLanguage();
            selfLang = ConfigManager.INSTANCE.getSelfLanguage();
            speakAsLang = ConfigManager.INSTANCE.getSpeakAsLanguage();
        }
        Keyboard.enableRepeatEvents(true);
        String btnSaveClose = I18n.format("translationmod.gui.settings.button.save_close");
        String btnResetDefault = I18n.format("translationmod.gui.settings.button.reset_default");
        String btnTranslateSign = I18n.format("translationmod.gui.settings.button.translate_sign");
        String btnEngineOptions = I18n.format("translationmod.gui.settings.button.engine_options");
        String btnMessageColor = I18n.format("translationmod.gui.settings.button.message_color");
        String btnViewAdd = I18n.format("translationmod.gui.settings.button.view_add");

        this.buttonList.add(new GuiButton(0, getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, btnSaveClose));
        this.buttonList.add(new GuiButton(1, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, btnResetDefault));
        this.buttonList.add(new GuiButton(2, getRightMargin(regularButtonWidth), getYOrigin() + 50, regularButtonWidth, regularButtonHeight, targetLang.getName()));
        this.buttonList.add(new GuiButton(3, getRightMargin(regularButtonWidth), getYOrigin() + 70, regularButtonWidth, regularButtonHeight, selfLang.getName()));
        this.buttonList.add(new GuiButton(4, getRightMargin(regularButtonWidth), getYOrigin() + 90, regularButtonWidth, regularButtonHeight, speakAsLang.getName()));
        this.buttonList.add(new GuiButton(5, getLeftMargin(), getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, regularButtonWidth, regularButtonHeight,
                translateSign ? EnumChatFormatting.GREEN + btnTranslateSign : EnumChatFormatting.RED + btnTranslateSign));
        this.buttonList.add(new GuiButton(6, getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, btnEngineOptions));
        this.buttonList.add(new GuiButton(7, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight,
                EnumChatFormatting.getValueByName(color) + btnMessageColor));
        this.buttonList.add(new GuiButton(8, getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 15 - regularButtonHeight * 3, smallButtonLength, smallButtonLength,
                bold ? "\u00a7a" + EnumChatFormatting.BOLD + "B" : "\u00a7c" + EnumChatFormatting.BOLD + "B"));
        this.buttonList.add(new GuiButton(9, getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, smallButtonLength, smallButtonLength,
                italic ? "\u00a7a" + EnumChatFormatting.ITALIC + "I" : "\u00a7c" + EnumChatFormatting.ITALIC + "I"));
        this.buttonList.add(new GuiButton(10, getLeftMargin() + regularButtonWidth + 10, getYOrigin() + guiHeight - 5 - regularButtonHeight, smallButtonLength, smallButtonLength,
                underline ? "\u00a7a" + EnumChatFormatting.UNDERLINE + "U" : "\u00a7c" + EnumChatFormatting.UNDERLINE + "U"));
        this.buttonList.add(new GuiButton(11, getRightMargin(regularButtonWidth), getYOrigin() + 20, regularButtonWidth, regularButtonHeight, btnViewAdd));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        Keyboard.enableRepeatEvents(false);
        switch (button.id) {
            case 0:
                applySettings();
                mc.displayGuiScreen(null);
                break;
            case 1:
                resetDefault();
                break;
            case 2:
                mc.displayGuiScreen(new LanguageSelectGui(this, 0));
                break;
            case 3:
                mc.displayGuiScreen(new LanguageSelectGui(this, 1));
                break;
            case 4:
                mc.displayGuiScreen(new LanguageSelectGui(this, 2));
                break;
            case 5:
                String translateSignText = I18n.format("translationmod.gui.settings.button.translate_sign");
                if (translateSign) {
                    button.displayString = EnumChatFormatting.RED + translateSignText;
                } else {
                    button.displayString = EnumChatFormatting.GREEN + translateSignText;
                }
                translateSign = !translateSign;
                break;
            case 6:
                mc.displayGuiScreen(new EngineGui());
                break;
            case 7:
                String messageColorText = I18n.format("translationmod.gui.settings.button.message_color");
                EnumChatFormatting formatColor = EnumChatFormatting.getValueByName(color);
                //Treat the formatting character as hex. Just so happens there are 16 colors and each are represented with a base 16 number
                int colorCode = formatColor.getColorIndex();
                colorCode++;
                colorCode = colorCode & 0xf;
                EnumChatFormatting newColor = EnumChatFormatting.func_175744_a(colorCode);
                color = newColor.getFriendlyName();
                this.buttonList.get(7).displayString = newColor + messageColorText;
                break;
            case 8:
                bold = !bold;
                this.buttonList.get(8).displayString = bold ? "\u00a7a" + EnumChatFormatting.BOLD + "B" : "\u00a7c" + EnumChatFormatting.BOLD + "B";
                break;
            case 9:
                italic = !italic;
                this.buttonList.get(9).displayString = italic ? "\u00a7a" + EnumChatFormatting.ITALIC + "I" : "\u00a7c" + EnumChatFormatting.ITALIC + "I";
                break;
            case 10:
                underline = !underline;
                this.buttonList.get(10).displayString = underline ? "\u00a7a" + EnumChatFormatting.UNDERLINE + "U" : "\u00a7c" + EnumChatFormatting.UNDERLINE + "U";
                break;
            case 11:
                mc.displayGuiScreen(new RegexGui());
                break;
        }
    }

    private void applySettings() {
        ConfigManager.INSTANCE.setTargetLanguage(targetLang);
        ConfigManager.INSTANCE.setSelfLanguage(selfLang);
        ConfigManager.INSTANCE.setSpeakAsLanguage(speakAsLang);
        ConfigManager.INSTANCE.setColor(color);
        ConfigManager.INSTANCE.setBold(bold);
        ConfigManager.INSTANCE.setItalic(italic);
        ConfigManager.INSTANCE.setUnderline(underline);
        ConfigManager.INSTANCE.setTranslateSign(translateSign);
        String settingsAppliedText = I18n.format("translationmod.chat.hint.settings_applied");

        ChatUtil.printChatMessage(true, settingsAppliedText, EnumChatFormatting.WHITE);
    }

    private void resetDefault() {
        color = "gray";
        bold = false;
        italic = false;
        underline = false;
        translateSign = true;
        selfLang = targetLang;
        targetLang = LangManager.getInstance().findLanguageFromName("English");
        speakAsLang = LangManager.getInstance().findLanguageFromName("Japanese");
        this.buttonList.get(2).displayString = "English";
        this.buttonList.get(3).displayString = "English";
        this.buttonList.get(4).displayString = "Japanese";
        this.buttonList.get(5).displayString = EnumChatFormatting.GREEN +
                I18n.format("translationmod.gui.settings.button.translate_sign");
        this.buttonList.get(7).displayString = EnumChatFormatting.getValueByName(color) +
                I18n.format("translationmod.gui.settings.button.message_color");
        this.buttonList.get(8).displayString = bold ? "\u00a7a" : "\u00a7c" + EnumChatFormatting.BOLD + "B";
        this.buttonList.get(9).displayString = italic ? "\u00a7a" : "\u00a7c" + EnumChatFormatting.ITALIC + "I";
        this.buttonList.get(10).displayString = underline ? "\u00a7a" : "\u00a7c" + EnumChatFormatting.UNDERLINE + "U";
    }
}
