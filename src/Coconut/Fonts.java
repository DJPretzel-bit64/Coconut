package Coconut;

import java.util.ArrayList;
import java.util.Objects;

public class Fonts {
	private static final ArrayList<Font> fonts = new ArrayList<>();

	public static void addFont(Font font) {
		fonts.add(font);
	}

	public static Font getFont(String name) {
		for(Font font : fonts)
			if(Objects.equals(font.getName(), name))
				return font;

		return null;
	}
}
