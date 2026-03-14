package foods;

import dto.FoodDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FoodController {

    public final FoodService foodService;


    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<List<FoodDTO.FoodResponse>> getAllFoods(){
        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodDTO.FoodResponse>> searchFoods(@RequestParam String q){
        return ResponseEntity.ok(foodService.searchFoods(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO.FoodResponse> getFoodById(@PathVariable Long id){
        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @PostMapping
    public ResponseEntity<FoodDTO.FoodResponse> createFood(@Valid @RequestBody FoodDTO.CreateFoodRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(foodService.createFood(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id){
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

}
