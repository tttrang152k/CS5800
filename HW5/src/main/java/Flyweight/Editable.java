package Flyweight;

import java.util.List;

public interface Editable {
    public void create(List<Text> text);
    public void edit(List<Text> text, String file);
    public void save(String file);
}
