package com.chat.e2e;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class RandomGenerators
{
    public static Color[] generateRandomDifferentColorsForGroupMemberNamesUsingRGB(int numberOfColors)
    {
        int threshold = 256 / (numberOfColors * 2);
        Color backgroundColor = (new JTextPane()).getBackground().brighter();
        Color[] generatedColors = new Color[numberOfColors];
        //int numberOfRandomGenerations = 0;
        for(int i = 0; i < generatedColors.length; i++)
        {
            boolean similarColor;
            Color generatedColor;
            do {
                similarColor = false;
                Random randomNumberGenerator = new Random();
                int r = randomNumberGenerator.nextInt(0, 256);
                int g = randomNumberGenerator.nextInt(0, 256);
                int b = randomNumberGenerator.nextInt(0, 256);
                generatedColor = new Color(r, g, b);
                //numberOfRandomGenerations++;
                if(Math.abs(generatedColor.getRed() - backgroundColor.getRed()) < threshold || Math.abs(generatedColor.getGreen() - backgroundColor.getGreen()) < threshold || Math.abs(generatedColor.getBlue() - backgroundColor.getBlue()) < threshold)
                {
                    similarColor = true;
                    continue;
                }
                for(int j = 0; j < i; j++)
                {
                    if(Math.abs(generatedColor.getRed() - generatedColors[j].getRed()) < threshold || Math.abs(generatedColor.getGreen() - generatedColors[j].getGreen()) < threshold || Math.abs(generatedColor.getBlue() - generatedColors[j].getBlue()) < threshold)
                    {
                        similarColor = true;
                        break;
                    }
                }
            }while(similarColor);
            generatedColors[i] = generatedColor;
        }

        //System.out.println("Number of random generations required: " + numberOfRandomGenerations);

        return generatedColors;
    }

    public static Color[] generateRandomDifferentColorsForGroupMemberNamesUsingHSB(int numberOfColors)
    {
        Color backgroundColor = (new JTextPane()).getBackground().brighter();
        float[] backgroundColorInHSB = Color.RGBtoHSB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), null);
        //System.out.println(Arrays.toString(backgroundColorInHSB));
        Color[] generatedColors = new Color[numberOfColors];
        float stepSize = 1f / (numberOfColors + 1);  //new Random().nextInt(numberOfColors / 2 + 1, numberOfColors + 1));
        float requiredSaturation = 1f; //(backgroundColorInHSB[1] + 0.5f) % 1f;
        float requiredBrightness; //(backgroundColorInHSB[2] + 0.5f) % 1f;
        if(backgroundColorInHSB[2] > 0.8f)
        {
            requiredBrightness = 0.8f;
        }
        else
        {
            requiredBrightness = 1f;
        }
        //float balancingStepFactor = 0;
        float currentHue = backgroundColorInHSB[0];  //balancingStepFactor * stepSize) % 1f;
        for(int i = 0; i < generatedColors.length; i++)
        {
            currentHue = (currentHue + stepSize) % 1f;
            generatedColors[i] = new Color(Color.HSBtoRGB(currentHue, requiredSaturation, requiredBrightness));
        }

        return generatedColors;
    }
}
