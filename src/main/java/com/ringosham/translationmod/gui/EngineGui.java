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
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.Minecraft.getMinecraft;

public class EngineGui extends CommonGui {
    private static final int guiWidth = 300;
    private static final int guiHeight = 150;

    private String engine;
    private GuiTextField googleKeyBox;
    private GuiTextField baiduKeyBox;
    private GuiTextField baiduAppIdBox;

    private static String getTitle() {
        return I18n.format("translationmod.gui.settings.title");
    }

    private static List<String> getGoogleTooltip() {
        return Arrays.asList(
                I18n.format("translationmod.engine_gui.tooltip.google.line1"),
                I18n.format("translationmod.engine_gui.tooltip.google.line2"),
                I18n.format("translationmod.engine_gui.tooltip.google.line3"),
                I18n.format("translationmod.engine_gui.tooltip.google.line4"),
                I18n.format("translationmod.engine_gui.tooltip.google.line5")
        );
    }

    private static List<String> getBaiduTooltip() {
        return Arrays.asList(
                I18n.format("translationmod.engine_gui.tooltip.baidu.line1"),
                I18n.format("translationmod.engine_gui.tooltip.baidu.line2"),
                I18n.format("translationmod.engine_gui.tooltip.baidu.line3"),
                I18n.format("translationmod.engine_gui.tooltip.baidu.line4"),
                I18n.format("translationmod.engine_gui.tooltip.baidu.line5"),
                I18n.format("translationmod.engine_gui.tooltip.baidu.line6")
        );
    }

    EngineGui() {
        super(guiHeight, guiWidth);
        engine = ConfigManager.INSTANCE.getTranslationEngine();
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        drawStringLine(getTitle(), new String[]{
                I18n.format("translationmod.engine_gui.text.choose_engine"),
                I18n.format("translationmod.engine_gui.text.only_one_engine")
        }, 5);
        switch (engine) {
            case "google":
                fontRendererObj.drawString(I18n.format("translationmod.engine_gui.text.google_api_key"),
                        getLeftMargin(), getYOrigin() + 75, 0x555555);
                googleKeyBox.drawTextBox();
                fontRendererObj.drawString(I18n.format("translationmod.engine_gui.text.google_free_hint"),
                        getLeftMargin(), getYOrigin() + 110, 0x555555);
                break;
            case "baidu":
                fontRendererObj.drawString(I18n.format("translationmod.engine_gui.text.baidu_app_id"),
                        getLeftMargin(), getYOrigin() + 65, 0x555555);
                baiduAppIdBox.drawTextBox();
                fontRendererObj.drawString(I18n.format("translationmod.engine_gui.text.baidu_api_key"),
                        getLeftMargin(), getYOrigin() + 95, 0x555555);
                baiduKeyBox.drawTextBox();
                break;
        }
        if (this.buttonList.get(0).isMouseOver())
            drawHoveringText(getGoogleTooltip(), x, y);
        if (this.buttonList.get(1).isMouseOver())
            drawHoveringText(getBaiduTooltip(), x, y);

    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.googleKeyBox = new GuiTextField(0, this.fontRendererObj, getLeftMargin(), getYOrigin() + 90, guiWidth - 10, 15);
        googleKeyBox.setCanLoseFocus(true);
        googleKeyBox.setMaxStringLength(84);
        googleKeyBox.setEnableBackgroundDrawing(true);
        googleKeyBox.setText(ConfigManager.INSTANCE.getGoogleKey());
        this.baiduAppIdBox = new GuiTextField(1, this.fontRendererObj, getLeftMargin(), getYOrigin() + 75, guiWidth - 10, 15);
        baiduAppIdBox.setCanLoseFocus(true);
        baiduAppIdBox.setMaxStringLength(20);
        baiduAppIdBox.setEnableBackgroundDrawing(true);
        baiduAppIdBox.setText(ConfigManager.INSTANCE.getBaiduAppId());
        this.baiduKeyBox = new GuiTextField(2, this.fontRendererObj, getLeftMargin(), getYOrigin() + 105, guiWidth - 10, 15);
        baiduKeyBox.setCanLoseFocus(true);
        baiduKeyBox.setEnableBackgroundDrawing(true);
        baiduKeyBox.setMaxStringLength(24);
        baiduKeyBox.setText(ConfigManager.INSTANCE.getBaiduKey());

        this.buttonList.add(new GuiButton(0, getLeftMargin(), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight,
                I18n.format("translationmod.engine_gui.button.google")));
        this.buttonList.add(new GuiButton(1, getRightMargin(guiWidth / 2 - 5), getYOrigin() + 40, guiWidth / 2 - 10, regularButtonHeight,
                I18n.format("translationmod.engine_gui.button.baidu")));
        this.buttonList.add(new GuiButton(2, getRightMargin(regularButtonWidth), getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight,
                I18n.format("translationmod.engine_gui.button.apply_close")));
        this.buttonList.add(new GuiButton(3, getRightMargin(regularButtonWidth) - regularButtonWidth - 5, getYOrigin() + guiHeight - regularButtonHeight - 5, regularButtonWidth, regularButtonHeight,
                I18n.format("translationmod.engine_gui.button.back")));switch (engine) {
            case "google":
                this.buttonList.get(0).enabled = false;
                break;
            case "baidu":
                this.buttonList.get(1).enabled = false;
                break;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                engine = "google";
                this.buttonList.get(0).enabled = false;
                this.buttonList.get(1).enabled = true;
                break;
            case 1:
                engine = "baidu";
                this.buttonList.get(0).enabled = true;
                this.buttonList.get(1).enabled = false;
                break;
            case 2:
                applyKey();
                break;
            case 3:
                configGui();
                break;
        }
        super.actionPerformed(button);
    }

    //These methods need to be overridden. Otherwise, Textboxes don't work.
    @Override
    public void mouseClicked(int x, int y, int state) throws IOException {
        super.mouseClicked(x, y, state);
        this.baiduAppIdBox.mouseClicked(x, y, state);
        this.baiduKeyBox.mouseClicked(x, y, state);
        this.googleKeyBox.mouseClicked(x, y, state);
    }

    @Override
    public void keyTyped(char typedchar, int keycode) throws IOException {
        if (this.baiduKeyBox.isFocused())
            this.baiduKeyBox.textboxKeyTyped(typedchar, keycode);
        if (this.baiduAppIdBox.isFocused())
            this.baiduAppIdBox.textboxKeyTyped(typedchar, keycode);
        if (this.googleKeyBox.isFocused())
            this.googleKeyBox.textboxKeyTyped(typedchar, keycode);
        super.keyTyped(typedchar, keycode);
    }

    private void configGui() {
        Keyboard.enableRepeatEvents(false);
        getMinecraft().displayGuiScreen(new ConfigGui());
    }

    private void applyKey() {
        Keyboard.enableRepeatEvents(true);
        ConfigManager.INSTANCE.setGoogleKey(googleKeyBox.getText());
        ConfigManager.INSTANCE.setBaiduAppId(baiduAppIdBox.getText());
        ConfigManager.INSTANCE.setBaiduKey(baiduKeyBox.getText());
        ConfigManager.INSTANCE.setTranslationEngine(engine);
        ConfigManager.INSTANCE.saveConfig();
        Log.logger.info("Saved engine options");
        ChatUtil.printChatMessage(true,
                I18n.format("translationmod.engine_gui.chat.applied"),
                EnumChatFormatting.WHITE);  getMinecraft().displayGuiScreen(null);
    }
}
