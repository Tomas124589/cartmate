package cz.orv0005.cartmate.models;

public class ShoppingListItem {
    private long id = 0;
    private final long idList;
    private final long idItem;
    private final String name;
    private final Integer count;

    public ShoppingListItem(Long idList, Long idItem, String name, Integer count) {
        this.idList = idList;
        this.idItem = idItem;
        this.name = name;
        this.count = count;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getIdList() {
        return idList;
    }

    public long getIdItem() {
        return idItem;
    }

    public String getName() {

        return name;
    }

    public Integer getCount() {
        return count;
    }
}
