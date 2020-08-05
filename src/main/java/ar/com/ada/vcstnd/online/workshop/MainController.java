package ar.com.ada.vcstnd.online.workshop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class MainController implements CommandLineRunner {

    private List<MovieOrSerie> moviesOrSeriesDataBaseFake;

    @GetMapping({ "", "/" }) @CrossOrigin(origins = "*")
    public ResponseEntity<List<MovieOrSerie>> getAllContent(
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<String> type) {

        List<MovieOrSerie> movieOrSeries = moviesOrSeriesDataBaseFake
                .stream()
                .filter(getFilterConditions(year, type))
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieOrSeries);
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        moviesOrSeriesDataBaseFake = mapper.readValue(
                new File("database.json"),
                new TypeReference<List<MovieOrSerie>>() {
                }
        );
    }

    private Predicate<MovieOrSerie> getFilterConditions(Optional<Integer> year, Optional<String> type) {
        // Se arme el contenedor que tendrá las condiciones de busqueda, inicialmente esta vacio
        List<Predicate<MovieOrSerie>> filterConditions = new ArrayList<Predicate<MovieOrSerie>>();

        // se agrega la condicion de busqueda si existe el filtro year
        filterConditions.add(movieOrSerie -> !year.isPresent() || movieOrSerie.getYear().equals(year.get()));

        // se agrega la condicion de busqueda si existe el filtro type
        filterConditions.add(movieOrSerie -> !type.isPresent() || movieOrSerie.getType().equals(type.get()));

        // Se devuelve el contenedor con las condiciones definidas
        return filterConditions.stream().reduce(movieOrSerie -> true, Predicate::and);
    }
}
