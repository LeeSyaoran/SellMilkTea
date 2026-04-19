package dao;

import model.Topping;
import java.util.List;

public interface ToppingDAO {
    List<Topping> findAll();
    Topping findById(int id);
    void insert(Topping topping);
    void update(Topping topping);
    void delete(int id);
}
