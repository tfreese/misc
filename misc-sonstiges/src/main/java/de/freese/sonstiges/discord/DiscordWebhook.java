// Created: 07.02.2021
package de.freese.sonstiges.discord;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;

/**
 * Class used to execute Discord Webhooks with low effort.<br>
 * https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb<br>
 *
 * @author Thomas Freese
 */
public class DiscordWebhook
{
    /**
     * @author Thomas Freese
     */
    public static class EmbedObject
    {
        /**
         * @author Thomas Freese
         */
        private static final class Author
        {
            /**
             *
             */
            private final String iconUrl;

            /**
             *
             */
            private final String name;

            /**
             *
             */
            private final String url;

            /**
             * Erstellt ein neues {@link Author} Object.
             *
             * @param name String
             * @param url String
             * @param iconUrl String
             */
            private Author(final String name, final String url, final String iconUrl)
            {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            /**
             * @return String
             */
            private String getIconUrl()
            {
                return this.iconUrl;
            }

            /**
             * @return String
             */
            private String getName()
            {
                return this.name;
            }

            /**
             * @return String
             */
            private String getUrl()
            {
                return this.url;
            }
        }

        /**
         * @author Thomas Freese
         */
        private static final class Field
        {
            /**
             *
             */
            private final boolean inline;

            /**
             *
             */
            private final String name;

            /**
             *
             */
            private final String value;

            /**
             * Erstellt ein neues {@link Field} Object.
             *
             * @param name String
             * @param value String
             * @param inline boolean
             */
            private Field(final String name, final String value, final boolean inline)
            {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            /**
             * @return String
             */
            private String getName()
            {
                return this.name;
            }

            /**
             * @return String
             */
            private String getValue()
            {
                return this.value;
            }

            /**
             * @return boolean
             */
            private boolean isInline()
            {
                return this.inline;
            }
        }

        /**
         * @author Thomas Freese
         */
        private static final class Footer
        {
            /**
             *
             */
            private final String iconUrl;

            /**
             *
             */
            private final String text;

            /**
             * Erstellt ein neues {@link Footer} Object.
             *
             * @param text String
             * @param iconUrl String
             */
            private Footer(final String text, final String iconUrl)
            {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            /**
             * @return String
             */
            private String getIconUrl()
            {
                return this.iconUrl;
            }

            /**
             * @return String
             */
            private String getText()
            {
                return this.text;
            }
        }

        /**
         * @author Thomas Freese
         */
        private static final class Image
        {
            /**
             *
             */
            private final String url;

            /**
             * Erstellt ein neues {@link Image} Object.
             *
             * @param url String
             */
            private Image(final String url)
            {
                this.url = url;
            }

            /**
             * @return String
             */
            private String getUrl()
            {
                return this.url;
            }
        }

        /**
         * @author Thomas Freese
         */
        private static final class Thumbnail
        {
            /**
             *
             */
            private final String url;

            /**
             * Erstellt ein neues {@link Thumbnail} Object.
             *
             * @param url String
             */
            private Thumbnail(final String url)
            {
                this.url = url;
            }

            /**
             * @return String
             */
            private String getUrl()
            {
                return this.url;
            }
        }

        /**
         *
         */
        private Author author;

        /**
         *
         */
        private Color color;

        /**
         *
         */
        private String description;

        /**
         *
         */
        private List<Field> fields = new ArrayList<>();

        /**
         *
         */
        private Footer footer;

        /**
         *
         */
        private Image image;

        /**
         *
         */
        private Thumbnail thumbnail;

        /**
         *
         */
        private String title;

        /**
         *
         */
        private String url;

        /**
         * @param name String
         * @param value String
         * @param inline boolean
         * @return EmbedObject
         */
        public EmbedObject addField(final String name, final String value, final boolean inline)
        {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        /**
         * @return Author
         */
        public Author getAuthor()
        {
            return this.author;
        }

        /**
         * @return {@link Color}
         */
        public Color getColor()
        {
            return this.color;
        }

        /**
         * @return String
         */
        public String getDescription()
        {
            return this.description;
        }

        /**
         * @return {@link List}
         */
        public List<Field> getFields()
        {
            return this.fields;
        }

        /**
         * @return Footer
         */
        public Footer getFooter()
        {
            return this.footer;
        }

        /**
         * @return Image
         */
        public Image getImage()
        {
            return this.image;
        }

        /**
         * @return Thumbnail
         */
        public Thumbnail getThumbnail()
        {
            return this.thumbnail;
        }

        /**
         * @return String
         */
        public String getTitle()
        {
            return this.title;
        }

        /**
         * @return String
         */
        public String getUrl()
        {
            return this.url;
        }

        /**
         * @param name String
         * @param url String
         * @param icon String
         * @return EmbedObject
         */
        public EmbedObject setAuthor(final String name, final String url, final String icon)
        {
            this.author = new Author(name, url, icon);
            return this;
        }

        /**
         * @param color {@link Color}
         * @return EmbedObject
         */
        public EmbedObject setColor(final Color color)
        {
            this.color = color;
            return this;
        }

        /**
         * @param description String
         * @return EmbedObject
         */
        public EmbedObject setDescription(final String description)
        {
            this.description = description;
            return this;
        }

        /**
         * @param text String
         * @param icon String
         * @return EmbedObject
         */
        public EmbedObject setFooter(final String text, final String icon)
        {
            this.footer = new Footer(text, icon);
            return this;
        }

        /**
         * @param url String
         * @return EmbedObject
         */
        public EmbedObject setImage(final String url)
        {
            this.image = new Image(url);
            return this;
        }

        /**
         * @param url String
         * @return EmbedObject
         */
        public EmbedObject setThumbnail(final String url)
        {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        /**
         * @param title String
         * @return EmbedObject
         */
        public EmbedObject setTitle(final String title)
        {
            this.title = title;
            return this;
        }

        /**
         * @param url String
         * @return EmbedObject
         */
        public EmbedObject setUrl(final String url)
        {
            this.url = url;
            return this;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class JSONObject
    {
        /**
         *
         */
        private final Map<String, Object> map = new HashMap<>();

        /**
         * @param key String
         * @param value Object
         */
        void put(final String key, final Object value)
        {
            if (value != null)
            {
                this.map.put(key, value);
            }
        }

        /**
         * @param string String
         * @return String
         */
        private String quote(final String string)
        {
            return "\"" + string + "\"";
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = this.map.entrySet();
            builder.append("{");

            int i = 0;

            for (Map.Entry<String, Object> entry : entrySet)
            {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String)
                {
                    builder.append(quote(String.valueOf(val)));
                }
                else if (val instanceof Integer)
                {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                }
                else if (val instanceof Boolean)
                {
                    builder.append(val);
                }
                else if (val instanceof JSONObject)
                {
                    builder.append(val.toString());
                }
                else if (val.getClass().isArray())
                {
                    builder.append("[");
                    int len = Array.getLength(val);

                    for (int j = 0; j < len; j++)
                    {
                        builder.append(Array.get(val, j).toString()).append(j != (len - 1) ? "," : "");
                    }

                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }
    }

    /**
     *
     */
    private String avatarUrl;

    /**
     *
     */
    private String content;

    /**
     *
     */
    private final List<EmbedObject> embeds = new ArrayList<>();

    /**
     *
     */
    private boolean tts;

    /**
     *
     */
    private final String url;

    /**
     *
     */
    private String username;

    /**
     * Constructs a new DiscordWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public DiscordWebhook(final String url)
    {
        this.url = url;
    }

    /**
     * @param embed {@link EmbedObject}
     */
    public void addEmbed(final EmbedObject embed)
    {
        this.embeds.add(embed);
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    public void execute() throws IOException
    {
        if ((this.content == null) && this.embeds.isEmpty())
        {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }

        JSONObject json = new JSONObject();

        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);

        if (!this.embeds.isEmpty())
        {
            List<JSONObject> embedObjects = new ArrayList<>();

            for (EmbedObject embed : this.embeds)
            {
                JSONObject jsonEmbed = new JSONObject();

                jsonEmbed.put("title", embed.getTitle());
                jsonEmbed.put("description", embed.getDescription());
                jsonEmbed.put("url", embed.getUrl());

                if (embed.getColor() != null)
                {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();

                    jsonEmbed.put("color", rgb);
                }

                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();

                if (footer != null)
                {
                    JSONObject jsonFooter = new JSONObject();

                    jsonFooter.put("text", footer.getText());
                    jsonFooter.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonFooter);
                }

                if (image != null)
                {
                    JSONObject jsonImage = new JSONObject();

                    jsonImage.put("url", image.getUrl());
                    jsonEmbed.put("image", jsonImage);
                }

                if (thumbnail != null)
                {
                    JSONObject jsonThumbnail = new JSONObject();

                    jsonThumbnail.put("url", thumbnail.getUrl());
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }

                if (author != null)
                {
                    JSONObject jsonAuthor = new JSONObject();

                    jsonAuthor.put("name", author.getName());
                    jsonAuthor.put("url", author.getUrl());
                    jsonAuthor.put("icon_url", author.getIconUrl());
                    jsonEmbed.put("author", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();

                for (EmbedObject.Field field : fields)
                {
                    JSONObject jsonField = new JSONObject();

                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());

                    jsonFields.add(jsonField);
                }

                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }

        URI uri = URI.create(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) uri.toURL().openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        connection.getInputStream().close(); // I'm not sure why but it doesn't work without getting the InputStream
        connection.disconnect();
    }

    /**
     * @param avatarUrl String
     */
    public void setAvatarUrl(final String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    /**
     * @param content String
     */
    public void setContent(final String content)
    {
        this.content = content;
    }

    /**
     * @param tts boolean
     */
    public void setTts(final boolean tts)
    {
        this.tts = tts;
    }

    /**
     * @param username String
     */
    public void setUsername(final String username)
    {
        this.username = username;
    }
}
