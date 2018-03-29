package com.production.outlau.translate;

import java.util.Hashtable;

public class Globals {

    //TODO
    /*
     *
     * Fix keyboard open when sliding to listview when firstfragment has expanded view
     *
     * Fix lag when translating more than 1 word!
     *
     * Widget
     * - UPDATE
     *
     * QuickTile
     *
     * Wait until translation is complete before adding word to list
     */

    public static boolean expanded = false;

    public static Hashtable<String, String> languages = new Hashtable<String, String>() {
        {
            put("auto", "Automatic");
            put("af", "Afrikaans");
            put("sq", "Albanian");
            put("am", "Amharic");
            put("ar", "Arabic");
            put("hy", "Armenian");
            put("az", "Azerbaijani");
            put("eu", "Basque");
            put("be", "Belarusian");
            put("bn", "Bengali");
            put("bs", "Bosnian");
            put("bg", "Bulgarian");
            put("ca", "Catalan");
            put("ceb", "Cebuano");
            put("ny", "Chichewa");
            put("zh-cn", "Chinese Simplified");
            put("zh-tw", "Chinese Traditional");
            put("co", "Corsican");
            put("hr", "Croatian");
            put("cs", "Czech");
            put("da", "Danish");
            put("nl", "Dutch");
            put("en", "English");
            put("eo", "Esperanto");
            put("et", "Estonian");
            put("tl", "Filipino");
            put("fi", "Finnish");
            put("fr", "French");
            put("fy", "Frisian");
            put("gl", "Galician");
            put("ka", "Georgian");
            put("de", "German");
            put("el", "Greek");
            put("gu", "Gujarati");
            put("ht", "Haitian Creole");
            put("ha", "Hausa");
            put("haw", "Hawaiian");
            put("iw", "Hebrew");
            put("hi", "Hindi");
            put("hmn", "Hmong");
            put("hu", "Hungarian");
            put("is", "Icelandic");
            put("ig", "Igbo");
            put("id", "Indonesian");
            put("ga", "Irish");
            put("it", "Italian");
            put("ja", "Japanese");
            put("jw", "Javanese");
            put("kn", "Kannada");
            put("kk", "Kazakh");
            put("km", "Khmer");
            put("ko", "Korean");
            put("ku", "Kurdish (Kurmanji)");
            put("ky", "Kyrgyz");
            put("lo", "Lao");
            put("la", "Latin");
            put("lv", "Latvian");
            put("lt", "Lithuanian");
            put("lb", "Luxembourgish");
            put("mk", "Macedonian");
            put("mg", "Malagasy");
            put("ms", "Malay");
            put("ml", "Malayalam");
            put("mt", "Maltese");
            put("mi", "Maori");
            put("mr", "Marathi");
            put("mn", "Mongolian");
            put("my", "Myanmar (Burmese)");
            put("ne", "Nepali");
            put("no", "Norwegian");
            put("ps", "Pashto");
            put("fa", "Persian");
            put("pl", "Polish");
            put("pt", "Portuguese");
            put("ma", "Punjabi");
            put("ro", "Romanian");
            put("ru", "Russian");
            put("sm", "Samoan");
            put("gd", "Scots Gaelic");
            put("sr", "Serbian");
            put("st", "Sesotho");
            put("sn", "Shona");
            put("sd", "Sindhi");
            put("si", "Sinhala");
            put("sk", "Slovak");
            put("sl", "Slovenian");
            put("so", "Somali");
            put("es", "Spanish");
            put("su", "Sundanese");
            put("sw", "Swahili");
            put("sv", "Swedish");
            put("tg", "Tajik");
            put("ta", "Tamil");
            put("te", "Telugu");
            put("th", "Thai");
            put("tr", "Turkish");
            put("uk", "Ukrainian");
            put("ur", "Urdu");
            put("uz", "Uzbek");
            put("vi", "Vietnamese");
            put("cy", "Welsh");
            put("xh", "Xhosa");
            put("yi", "Yiddish");
            put("yo", "Yoruba");
            put("zu", "Zulu");
        }
    };
}