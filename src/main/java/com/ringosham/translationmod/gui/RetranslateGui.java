package com.ringosham.translationmod.gui;

import com.ringosham.translationmod.translate.Translator;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
public class RetranslateGui extends CommonGui {
    private static final String title;
    private static final int guiHeight;
    private static final int guiWidth;

    static {
        // 标题改为从lang文件读取（初始化时先占位，实际使用时动态获取）
        title = I18n.format("translationmod.gui.retranslate.title");
        guiHeight = 200;
        guiWidth = 350;
    }

    private final List<Translator.TranslationLog> logs;

    public RetranslateGui() {
        super(guiHeight, guiWidth);
        //Cache the log within this gui instance. As the chat will overwrite the log.
        logs = Translator.getTranslationLog(15);
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        String retranslateTitle = I18n.format("translationmod.gui.retranslate.title");
        String hintLine1 = I18n.format("translationmod.gui.retranslate.hint.line1");
        String hintLine2 = I18n.format("translationmod.gui.retranslate.hint.line2");
        drawStringLine(retranslateTitle, new String[]{
                hintLine1,
                hintLine2,
        }, 0);
        for (int i = 0; i < buttonList.size(); i++) {
            TextButton button = (TextButton) buttonList.get(i);
            if (button.isMouseOver()) {
                List<String> hoverText = new ArrayList<>();
                // 悬浮提示文本从lang读取，使用%s占位符填充动态内容
                hoverText.add(I18n.format("translationmod.gui.retranslate.hover.sender", logs.get(i).getSender()));
                hoverText.add(I18n.format("translationmod.gui.retranslate.hover.message", logs.get(i).getMessage()));
                //func_243308_b(MatrixStack, List<ITextComponent>, int, int) -> renderTooltip(...)
                drawHoveringText(hoverText, x, y);
            }
        }
    }

    @Override
    public void initGui() {
        int index = 0;
        int offset = 0;
        for (Translator.TranslationLog log : logs) {
            String buttonText = log.getMessage();
            if (getTextWidth(buttonText) > guiWidth - 15) {
                buttonText = buttonText + "...";
                while (getTextWidth(buttonText) > guiWidth - 15)
                    buttonText = buttonText.substring(0, buttonText.length() - 4) + "...";
            }
            this.buttonList.add(new TextButton(index, getLeftMargin(), getTopMargin() + 40 + offset, getTextWidth(buttonText), buttonText, 0));
            offset += 10;
            index++;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        selectLanguage(logs.get(button.id).getSender(), logs.get(button.id).getMessage());
    }

    private void selectLanguage(String sender, String message) {
        mc.displayGuiScreen(new LanguageSelectGui(sender, message));
    }
}
