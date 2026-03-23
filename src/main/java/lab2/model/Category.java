package lab2.model;

import jakarta.validation.constraints.NotBlank;

public class Category {
    private Long id;

    @NotBlank(message = "Назва категорії не може бути порожньою")
    private String name;

    public Category() {}

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}