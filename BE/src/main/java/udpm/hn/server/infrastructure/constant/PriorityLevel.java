package udpm.hn.server.infrastructure.constant;

public enum PriorityLevel {

    QUAN_TRONG, // 0
    CAO, // 1
    TRUNG_BINH, // 2
    THAP; // 3

    public int getLevel() {
        return this.ordinal(); // ordinal() trả về chỉ mục của enum (0, 1, 2, 3)
    }



}
