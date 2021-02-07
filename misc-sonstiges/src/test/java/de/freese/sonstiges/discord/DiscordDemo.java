// Created: 07.02.2021
package de.freese.sonstiges.discord;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public class DiscordDemo
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        String webHookId = args[0];
        String webHookToken = args[1];

        String urLString = String.format("https://discord.com/api/webhooks/%s/%s", webHookId, webHookToken);
        String iconUrl = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png";

        DiscordWebhook webhook = new DiscordWebhook(urLString);
        webhook.setUsername("Custom Username!");
        webhook.setContent("Any message!");
        webhook.setAvatarUrl("https://avatars.githubusercontent.com/u/1973918?v=4");
        webhook.setTts(false);

        // @formatter:off
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.RED)
                .setAuthor("Author Name", "https://discord.com/developers/docs/resources/webhook", iconUrl)
                .setTitle("Title").setDescription("This is a description").setUrl("https://google.de")
                .addField("1st Field", "Inline", true)
                .addField("2nd Field", "Inline", true)
                .addField("3rd Field", "No-Inline", false)
                .setThumbnail(iconUrl) // Kleines Bild oben Rechts
                .setImage(iconUrl) // Großes Bild
                .setFooter("Footer text", iconUrl));
        // @formatter:on

        webhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription("Just another added embed object!"));
        webhook.execute();
    }
}
