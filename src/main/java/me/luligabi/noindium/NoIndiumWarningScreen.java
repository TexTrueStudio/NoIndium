package me.luligabi.noindium;

import me.luligabi.noindium.mixin.WarningScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.io.File;

@Environment(EnvType.CLIENT)
public class NoIndiumWarningScreen extends WarningScreen {

    public NoIndiumWarningScreen() {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT, null);
    }

    @Override
    protected void initButtons(int yOffset) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, NoIndium.HAS_SODIUM ? CURSEFORGE : OPEN_MODS_FOLDER, buttonWidget ->  {
            if(NoIndium.HAS_SODIUM) Util.getOperatingSystem().open("https://www.curseforge.com/minecraft/mc-mods/indium");
            else Util.getOperatingSystem().open(new File(FabricLoader.getInstance().getGameDir().toFile(), "mods"));
        }));

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, 100 + yOffset, 150, 20, NoIndium.HAS_SODIUM ? MODRINTH : OPTIFINE_ALTERNATIVES, buttonWidget ->  {
            Util.getOperatingSystem().open(NoIndium.HAS_SODIUM ? "https://modrinth.com/mod/indium" : "https://lambdaurora.dev/optifine_alternatives/");
        }));

        if(NoIndium.CONFIG.allowToProceed) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 75, 130 + yOffset, 150, 20, new TranslatableText("label.noindium.proceed"), buttonWidget ->  {
                if(this.checkbox.isChecked()) {
                    if(NoIndium.HAS_SODIUM) {
                        NoIndium.CONFIG.showIndiumScreen = false;
                    } else {
                        NoIndium.CONFIG.showOptifabricScreen = false;
                    }
                    NoIndium.CONFIG.save();
                }
                this.client.setScreen(new TitleScreen(false));
            }));
        }
    }

    @Override
    protected void init() {
        ((WarningScreenAccessor) this).setMessageText(MultilineText.create(textRenderer, MESSAGE, width - 50));
        int yOffset = (((WarningScreenAccessor) this).getMessageText().count() + 1) * textRenderer.fontHeight * 2 - 20;
        if(NoIndium.CONFIG.allowToProceed) {
            checkbox = new CheckboxWidget(width / 2 - 155 + 80, 76 + yOffset, 150, 20, CHECK_MESSAGE, false);
            addDrawableChild(checkbox);
        }
        initButtons(yOffset);
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private static final MutableText HEADER = new TranslatableText(NoIndium.HAS_SODIUM ? "header.noindium.indium" : "header.noindium.optifabric").formatted(Formatting.DARK_RED, Formatting.BOLD);
    private static final TranslatableText MESSAGE = new TranslatableText(NoIndium.HAS_SODIUM ? "message.noindium.indium" : "message.noindium.optifabric");
    private static final TranslatableText CHECK_MESSAGE = new TranslatableText("multiplayerWarning.check");
    private static final MutableText NARRATED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);

    private static final LiteralText CURSEFORGE = new LiteralText("CurseForge");
    private static final LiteralText MODRINTH = new LiteralText("Modrinth");

    private static final TranslatableText OPEN_MODS_FOLDER = new TranslatableText("label.noindium.open_mods_folder");
    private static final TranslatableText OPTIFINE_ALTERNATIVES = new TranslatableText("label.noindium.optifine_alternatives");

}