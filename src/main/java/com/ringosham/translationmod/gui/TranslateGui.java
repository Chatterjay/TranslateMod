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

import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.translate.SelfTranslate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.resources.I18n;
import java.io.IOException;
import net.minecraft.client.resources.I18n;
public class TranslateGui extends CommonGui {
    private static final int guiHeight = 125;
    private static final int guiWidth = 225;
    private GuiTextField headerField;
    private GuiTextField messageField;

    public TranslateGui() {
        super(guiHeight, guiWidth);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        String modNameText = I18n.format("translationmod.gui.mod_name", "Ringosham");
        fontRendererObj.drawString(modNameText, getLeftMargin(), getTopMargin(), 0x555555);

        // 2. 命令/前缀输入提示
        String headerHintText = I18n.format("translationmod.gui.header_hint");
        fontRendererObj.drawString(headerHintText, getLeftMargin(), getTopMargin() + 10, 0x555555);

        // 3. 消息输入提示
        String messageHintText = I18n.format("translationmod.gui.message_hint");
        fontRendererObj.drawString(messageHintText, getLeftMargin(), getTopMargin() + 40, 0x555555);
        headerField.drawTextBox();
        messageField.drawTextBox();
        if (this.headerField.isFocused())
            this.messageField.setFocused(false);
        if (this.messageField.isFocused())
            this.headerField.setFocused(false);
    }

    @Override
    public void initGui() {
        this.headerField = new GuiTextField(0, this.fontRendererObj, getLeftMargin(), getYOrigin() + 25, guiWidth - 10, 15);
        this.messageField = new GuiTextField(1, this.fontRendererObj, getLeftMargin(), getYOrigin() + 55, guiWidth - 10, 15);
        headerField.setMaxStringLength(25);
        headerField.setCanLoseFocus(true);
        headerField.setEnableBackgroundDrawing(true);
        messageField.setMaxStringLength(75);
        messageField.setCanLoseFocus(true);
        messageField.setEnableBackgroundDrawing(true);
        messageField.setFocused(true);
        Keyboard.enableRepeatEvents(true);

        String btnSettings = I18n.format("translationmod.gui.button.settings");
        String btnClose = I18n.format("translationmod.gui.button.close");
        String btnCredits = I18n.format("translationmod.gui.button.credits");
        String btnRetranslate = I18n.format("translationmod.gui.button.retranslate");
        this.buttonList.add(new GuiButton(0, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, btnSettings));
        this.buttonList.add(new GuiButton(1, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, btnClose));
        this.buttonList.add(new GuiButton(2, getLeftMargin(), getYOrigin() + guiHeight - 10 - regularButtonHeight * 2, regularButtonWidth, regularButtonHeight, btnCredits));
        this.buttonList.add(new GuiButton(3, getLeftMargin(), getYOrigin() + guiHeight - 5 - regularButtonHeight, regularButtonWidth, regularButtonHeight, btnRetranslate));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        Keyboard.enableRepeatEvents(false);
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new ConfigGui());
                break;
            case 1:
                mc.displayGuiScreen(null);
                break;
            case 2:
                ChatUtil.printCredits();
                mc.displayGuiScreen(null);
                break;
            case 3:
                mc.displayGuiScreen(new RetranslateGui());
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.headerField.textboxKeyTyped(typedChar, keyCode);
        this.messageField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_RETURN && (this.messageField.isFocused() || this.headerField.isFocused())) {
            mc.displayGuiScreen(null);
            Thread translate = new SelfTranslate(this.messageField.getText(), this.headerField.getText());
            translate.start();
        }
        if (keyCode == Keyboard.KEY_TAB && this.messageField.isFocused() && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            this.headerField.setFocused(true);
            this.messageField.setFocused(false);
        } else if (keyCode == Keyboard.KEY_TAB && this.headerField.isFocused()) {
            this.headerField.setFocused(false);
            this.messageField.setFocused(true);
        }
        if (keyCode == Keyboard.KEY_E && !this.messageField.isFocused() && !this.headerField.isFocused())
            mc.displayGuiScreen(null);
        else
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int x, int y, int state) throws IOException {
        super.mouseClicked(x, y, state);
        this.headerField.mouseClicked(x, y, state);
        this.messageField.mouseClicked(x, y, state);
    }
}
