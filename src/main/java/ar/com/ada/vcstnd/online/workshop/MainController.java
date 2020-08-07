package ar.com.ada.vcstnd.online.workshop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class MainController {

    // se definie una varable de clase para cargar los datos de un archivo
    // esto hace la emulacion de una base de datos.
    private List<MovieOrSerie> dataBaseFake;

    @GetMapping({ "", "/" }) @CrossOrigin(origins = "*")
    public ResponseEntity<List<MovieOrSerie>> getAllContent(
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<String> type) {
        
        // se ejecuta la carga de la propiedad dataBaseFake con los datos del archivo database.json
        this.run();

        // se recorre la lista de series y/o peliculas, se aplica el filtro y el
        // resultdo lo guarda en una lista nueva (movieOrSeries)
        List<MovieOrSerie> movieOrSeries = dataBaseFake
                .stream()
                .filter(getFilterConditions(year, type))
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieOrSeries);
    }

    public void run(String... args) throws Exception {
        // este objete permite convertir del archivo a un objeto java
        ObjectMapper mapper = new ObjectMapper();

        // proceso que leee el archivo donde esta el listado de peliculas y serias
        // y lo almacena en la lista dataBaseFake
        dataBaseFake = mapper.readValue(
                new File("database.json"),
                new TypeReference<List<MovieOrSerie>>() {
                }
        );
    }

    private Predicate<MovieOrSerie> getFilterConditions(Optional<Integer> yearOpt, Optional<String> typeOpt) {
        // Se arme el contenedor que tendrá las condiciones de busqueda, inicialmente esta vacio
        List<Predicate<MovieOrSerie>> filterConditions = new ArrayList<Predicate<MovieOrSerie>>();

        // se agrega la condicion de busqueda si existe el filtro year
        yearOpt.ifPresent(year -> filterConditions.add(movieOrSerie -> movieOrSerie.getYear().equals(year)));

        // se agrega la condicion de busqueda si existe el filtro type
        typeOpt.ifPresent(type -> filterConditions.add(movieOrSerie -> movieOrSerie.getType().equals(type)));

        // Se devuelve el contenedor con las condiciones definidas
        return filterConditions.stream().reduce(movieOrSerie -> true, Predicate::and);
    }
}
