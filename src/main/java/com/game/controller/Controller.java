package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;

@RestController
public class Controller {

    private final PlayerService playerService;

    @Autowired
    public Controller(PlayerService playerService) {
        this.playerService = playerService;
    }


    @RequestMapping(path = "/rest/players", method = RequestMethod.GET)
    public ResponseEntity<List<Player>> getPlayersList(String name,
                                                       String title,
                                                       Race race,
                                                       Profession profession,
                                                       Long after,
                                                       Long before,
                                                       Boolean banned,
                                                       Integer minExperience,
                                                       Integer maxExperience,
                                                       Integer minLevel,
                                                       Integer maxLevel,
                                                       PlayerOrder order,
                                                       Integer pageNumber,
                                                       Integer pageSize) {
        List<Player> result = null;
        List<Player> allFilteredPlayers = null;
        try {
            allFilteredPlayers = playerService.getFilteredPlayers(name, title, race, profession,
                    after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        }catch (Exception ignore){
            
        }
        if (order != null){
            result =  playerService.sortPlayersList(allFilteredPlayers, order);
        } else result = allFilteredPlayers;
        List<Player> pageList = playerService.getPage(result, pageNumber, pageSize);

        return result != null //&& !result.isEmpty()
                ? new ResponseEntity<>(pageList, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


   @RequestMapping(path = "/rest/players/count", method = RequestMethod.GET)
   public ResponseEntity<Integer> getCountPlayers(String name,
                                                  String title,
                                                  Race race,
                                                  Profession profession,
                                                  Long after,
                                                  Long before,
                                                  Boolean banned,
                                                  Integer minExperience,
                                                  Integer maxExperience,
                                                  Integer minLevel,
                                                  Integer maxLevel,
                                                  PlayerOrder order,
                                                  Integer pageNumber,
                                                  Integer pageSize){
        List<Player> result = playerService.getFilteredPlayers(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        return result != null
                ? new ResponseEntity<>(playerService.getCountPlayers(result), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }

   @RequestMapping(path = "/rest/players", method = RequestMethod.POST)
   public ResponseEntity<Player> createPlayer(@RequestBody Player player){

        final Player newPlayer = playerService.createPlayer(player);
        return newPlayer != null
                ? new ResponseEntity<>(newPlayer,HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
   }

   @RequestMapping(path = "/rest/players/{id}", method = RequestMethod.GET)
   public ResponseEntity<Player> getPlayer(@PathVariable(value = "id") String pathId){
       Long id;
        try {
            id = Long.valueOf(pathId);
            if (!PlayerServiceImp.isIdValid(id)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (playerService.isAvailableFromDb(id)) {
            final Player resultPlayer = playerService.getById(id);
            return new ResponseEntity<>(resultPlayer, HttpStatus.OK);
        }   else return new ResponseEntity<>(HttpStatus.NOT_FOUND);

   }

    @RequestMapping(path = "/rest/players/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Player> updatePlayer(
            @PathVariable(value = "id") String pathId, @RequestBody Player player) {

        final Long id = Long.valueOf(pathId);
        try {
            final Player updatedPlayer = playerService.getById(id);
        }catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!PlayerServiceImp.isIdValid(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!playerService.isAvailableFromDb(id)){
            return (new ResponseEntity<>(HttpStatus.NOT_FOUND));}

        final Player result = playerService.updatePlayer(id, player);
        if (result == null){ return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @RequestMapping(path = "/rest/players/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Player> deletePlayer(@PathVariable(value = "id") String pathId){
        Long id = null;
        try {
            id = Long.valueOf(pathId);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!PlayerServiceImp.isIdValid(id)) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        Player playerToDel = playerService.getById(id);
        if (playerService.isAvailableFromDb(id)){
            playerService.deletePlayer(playerToDel);
            return (new ResponseEntity<>(HttpStatus.OK));
        }else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
