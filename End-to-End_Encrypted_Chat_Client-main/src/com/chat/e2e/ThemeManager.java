package com.chat.e2e;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import javax.swing.*;
import java.awt.*;

public class ThemeManager
{
    public static void initializeTheme()
    {
        changeTheme(ConfigManager.getCurrentTheme(), true);

        //UIManager.put( "Button.arc", 99 );
        //UIManager.put( "Panel.arc", 99 );
        UIManager.put( "ScrollBar.showButtons", true );
        //UIManager.put( "Label.font", new Font("Calibri", Font.PLAIN, 12) );
        UIManager.put( "MenuItem.minimumIconSize", new Dimension() );
        //UIManager.put("Label.arc", 999);

        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

        //FlatAnimatedLafChange.duration = 500;
        //FlatAnimatedLafChange.resolution = 50;
    }

    public static void changeTheme(String newTheme, boolean animated)
    {
        try
        {
            if(animated)
                FlatAnimatedLafChange.showSnapshot();
            boolean rehighlightActiveChat = false;
            if(WindowManager.getCurrentPanel().equals("UserChatPanel") && WindowManager.getChatPanelReference().getActiveChatComponentReference() != null)
            {
                rehighlightActiveChat = true;
            }
            if(rehighlightActiveChat)
            {
                WindowManager.getChatPanelReference().getActiveChatComponentReference().setActiveStatus(false);
                WindowManager.getChatPanelReference().getActiveChatComponentReference().dehighlightPanel();
            }

            if (newTheme.equals("FlatLaf Light"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            else if (newTheme.equals("FlatLaf Dark"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            else if (newTheme.equals("FlatLaf IntelliJ"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatIntelliJLaf");
            else if (newTheme.equals("FlatLaf Darcula"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
            else if (newTheme.equals("Arc"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcIJTheme");
            else if (newTheme.equals("Arc - Orange"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme");
            else if (newTheme.equals("Arc Dark"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme");
            else if (newTheme.equals("Arc Dark - Orange"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme");
            else if (newTheme.equals("Carbon"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme");
            else if (newTheme.equals("Cobalt 2"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme");
            else if (newTheme.equals("Cyan Light"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme");
            else if (newTheme.equals("Dark Flat"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme");
            else if (newTheme.equals("Dark Purple"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme");
            else if (newTheme.equals("Dracula"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme");
            else if (newTheme.equals("Gradianto Dark Fuchsia"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme");
            else if (newTheme.equals("Gradianto Deep Ocean"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme");
            else if (newTheme.equals("Gradianto Midnight Blue"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme");
            else if (newTheme.equals("Gradianto Nature Green"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme");
            else if (newTheme.equals("Gray"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme");
            else if (newTheme.equals("Gruvbox Dark Hard"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme");
            else if (newTheme.equals("Gruvbox Dark Medium"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme");
            else if (newTheme.equals("Gruvbox Dark Soft"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme");
            else if (newTheme.equals("Hiberbee Dark"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme");
            else if (newTheme.equals("High contrast"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme");
            else if (newTheme.equals("Light Flat"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme");
            else if (newTheme.equals("Material Design Dark"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme");
            else if (newTheme.equals("Monocai"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme");
            else if (newTheme.equals("Monokai Pro"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme");
            else if (newTheme.equals("Nord"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatNordIJTheme");
            else if (newTheme.equals("One Dark"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
            else if (newTheme.equals("Solarized Dark"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme");
            else if (newTheme.equals("Solarized Light"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme");
            else if (newTheme.equals("Spacegray"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme");
            else if (newTheme.equals("Vuesion"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme");
            else if (newTheme.equals("Xcode-Dark"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme");
            else if (newTheme.equals("Arc Dark (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme");
            else if (newTheme.equals("Atom One Dark (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme");
            else if (newTheme.equals("Atom One Light (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme");
            else if (newTheme.equals("Dracula (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme");
            else if (newTheme.equals("GitHub (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme");
            else if (newTheme.equals("GitHub Dark (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme");
            else if (newTheme.equals("Light Owl (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme");
            else if (newTheme.equals("Material Darker (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme");
            else if (newTheme.equals("Material Deep Ocean (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme");
            else if (newTheme.equals("Material Lighter (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme");
            else if (newTheme.equals("Material Oceanic (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme");
            else if (newTheme.equals("Material Palenight (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme");
            else if (newTheme.equals("Monokai Pro (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme");
            else if (newTheme.equals("Moonlight (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme");
            else if (newTheme.equals("Night Owl (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlIJTheme");
            else if (newTheme.equals("Solarized Dark (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme");
            else if (newTheme.equals("Solarized Light (Material)"))
                UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme");

            FlatLaf.updateUI();

            if(rehighlightActiveChat)
            {
                WindowManager.getChatPanelReference().getActiveChatComponentReference().highlightPanel();
                WindowManager.getChatPanelReference().getActiveChatComponentReference().setActiveStatus(true);
            }
            if(animated)
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }
        catch(Exception themeChangeError)
        {
            System.out.println("Theme change failed: " + newTheme + "\n" + themeChangeError);
            return;
        }
    }
}
