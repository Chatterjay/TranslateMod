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

package com.ringosham.translationmod.events;

import com.ringosham.translationmod.TranslationMod;
import com.ringosham.translationmod.common.ChatUtil;
import com.ringosham.translationmod.common.ConfigManager;
import com.ringosham.translationmod.common.Log;
import com.ringosham.translationmod.gui.TranslateGui;
import com.ringosham.translationmod.translate.SignTranslate;
import com.ringosham.translationmod.translate.Translator;
import com.ringosham.translationmod.translate.types.SignText;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.util.StatCollector;
import java.lang.reflect.Field;
import net.minecraft.client.resources.I18n;
public class Handler {
    private static Thread readSign;
    private SignText lastSign;
    private boolean hintShown = false;
    private int ticks = 0;
    private int refreshChatTicks = 0;
    private String chatGUIClassName = "";
    private Field scrollPos = null;
    private Field isScrolled = null;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) {
            Keyboard.enableRepeatEvents(false);
        }
    }

    @SubscribeEvent
    public void chatReceived(ClientChatReceivedEvent event) {
        IChatComponent eventMessage = event.message;
        String message = eventMessage.getUnformattedText().replaceAll("§(.)", "");
        Thread translate = new Translator(message, null, ConfigManager.INSTANCE.getTargetLanguage(), eventMessage);
        translate.start();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null)
            return;
        if (!hintShown) {
            hintShown = true;
            String keyName = Keyboard.getKeyName(KeyBind.translateKey.getKeyCode());
            String translationHint = I18n.format(
                    "translationmod.hint.translation_settings",
                    EnumChatFormatting.AQUA + keyName + EnumChatFormatting.WHITE
            );
            ChatUtil.printChatMessage(true, translationHint, EnumChatFormatting.WHITE);
            if (ConfigManager.INSTANCE.getRegexList().isEmpty()) {
                Log.logger.warn(I18n.format("translationmod.log.regex.missing"));
                String warningText = I18n.format("translationmod.chat.regex.missing");
                ChatUtil.printChatMessage(true, warningText, EnumChatFormatting.RED);
            }
        }
        if (TranslationMod.refreshChat > 0) {
            this.refreshChatTicks ++;
            if (this.refreshChatTicks >= 3) {
                this.refreshChatTicks = 0;
                if (Minecraft.getMinecraft().ingameGUI != null) {
                    GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    if (!this.chatGUIClassName.equals(chat.getClass().getName())) {
                        this.chatGUIClassName = chat.getClass().getName();
                        try {
                            this.scrollPos = chat.getClass().getDeclaredField("scrollPos");
                            this.scrollPos.setAccessible(true);
                        } catch (NoSuchFieldException ignore) {
                            try {
                                this.scrollPos = chat.getClass().getDeclaredField("field_146250_j");
                                this.scrollPos.setAccessible(true);
                            } catch (NoSuchFieldException ignore1) {
                                this.scrollPos = null;
                            }
                        }
                        try {
                            this.isScrolled = chat.getClass().getDeclaredField("isScrolled");
                            this.isScrolled.setAccessible(true);
                        } catch (NoSuchFieldException ignore) {
                            try {
                                this.isScrolled = chat.getClass().getDeclaredField("field_146251_k");
                                this.isScrolled.setAccessible(true);
                            } catch (NoSuchFieldException ignore1) {
                                this.isScrolled = null;
                            }
                        }
                    }
                    try {
                        int scrollPos = (int) this.scrollPos.get(chat);
                        boolean isScrolled = (boolean) this.isScrolled.get(chat);
                        chat.refreshChat();
                        if (scrollPos > 0) {
                            chat.scroll(scrollPos + TranslationMod.refreshChat);
                        }
                        this.isScrolled.set(chat, isScrolled);
                    } catch (NullPointerException | IllegalAccessException ignore) {

                    }
                }
                TranslationMod.refreshChat = 0;
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world == null)
            return;
        //Scan for signs
        if (ConfigManager.INSTANCE.isTranslateSign() && event.phase == TickEvent.Phase.END) {
            processSign(event.world);
        }
    }

    //If config is somehow changed through other means
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        ConfigManager.INSTANCE.saveConfig();
    }

    @SubscribeEvent
    public void onKeybind(InputEvent.KeyInputEvent event) {
        if (KeyBind.translateKey.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new TranslateGui());
    }

    private void processSign(World world) {
        MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver == null)
            return;
        else if (mouseOver.typeOfHit != MovingObjectType.BLOCK) {
            lastSign = null;
            ticks = 0;
            return;
        }
        BlockPos pos = mouseOver.getBlockPos();
        //Ignore air tiles
        if (world.getBlockState(pos).getBlock() == Block.getBlockById(0))
            return;
        //Wall signs and standing signs
        if (world.getBlockState(pos).getBlock() == Block.getBlockById(63) || world.getBlockState(pos).getBlock() == Block.getBlockById(68)) {
            //Ensure the player is staring at the same sign
            if (lastSign != null && lastSign.getText() != null) {
                if (lastSign.sameSign(pos)) {
                    ticks++;
                    //Count number of ticks the player is staring
                    //Assuming 20 TPS
                    if (ticks >= 20)
                        if (readSign != null && readSign.getState() == Thread.State.NEW)
                            readSign.start();
                } else
                    readSign = getSignThread(world, pos);
            } else
                readSign = getSignThread(world, pos);
        } else {
            lastSign = null;
            ticks = 0;
        }
    }

    private SignTranslate getSignThread(World world, BlockPos pos) {
        StringBuilder text = new StringBuilder();
        //Four lines of text in signs
        for (int i = 0; i < 4; i++) {
            String line = ((TileEntitySign) world.getTileEntity(pos)).signText[i].getUnformattedText();
            //Combine each line of the sign with spaces.
            //Due to differences between languages, this may break asian languages. (Words don't separate with spaces)
            text.append(" ").append(line);
        }
        text = new StringBuilder(text.toString().replaceAll("§(.)", ""));
        if (text.length() == 0)
            return null;
        lastSign = new SignText();
        lastSign.setSign(text.toString(), pos);
        return new SignTranslate(text.toString(), pos);
    }
}
