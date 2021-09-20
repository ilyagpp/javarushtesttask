package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.*;

@Service
@Transactional
public class PlayerServiceImp implements PlayerService{



    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImp(PlayerRepository playerRepository) {

        this.playerRepository = playerRepository;
    }


    @Override
    public List<Player> getPlayers() {

        return playerRepository.findAll();

    }
    @Override
    public List<Player> getFilteredPlayers(                String name,
                                                           String title,
                                                           Race race,
                                                           Profession profession,
                                                           Long after,
                                                           Long before,
                                                           Boolean banned,
                                                           Integer minExperience,
                                                           Integer maxExperience,
                                                           Integer minLevel,
                                                           Integer maxLevel) {

        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);

        final List<Player> allFilteredPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(player ->{
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (after != null && player.getBirthday().before(afterDate)) return;
            if (before!= null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned() != banned) return;
            if (minExperience != null && player.getExperience() < minExperience) return;
            if (maxExperience != null && player.getExperience() > maxExperience) return;
            if (minLevel != null && player.getLevel() < minLevel) return;
            if (maxLevel != null && player.getLevel() > maxLevel) return;

            allFilteredPlayers.add(player);
        });

            return allFilteredPlayers;

    }

    @Override
    public List<Player> getPage(List<Player> players, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3    : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > players.size()) to = players.size();
        return players.subList(from, to);
    }

    @Override
    public List<Player> sortPlayersList(List<Player> players, PlayerOrder order) {
        if (order != null) {
            players.sort((ship1, ship2) -> {
                switch (order) {
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case NAME: return ship1.getName().compareTo(ship2.getName());
                    case EXPERIENCE: return ship1.getExperience().compareTo(ship2.getExperience());
                    case BIRTHDAY: return ship1.getBirthday().compareTo(ship2.getBirthday());
                    default: return 0;
                }
            });
        }
        return players;
    }


    @Override
    public Integer getCountPlayers(List<Player> playerList) {
        return playerList.size();
    }

    @Override
    public Player createPlayer(Player player) {

        if (isPlayerValid(player)) {
            player.setLevel(playerLevel(player.getExperience()));
            player.setUntilNextLevel(untilNextLevel(player.getExperience()));
            return playerRepository.save(player);
        }
        return null;
    }

    @Override
    public Player updatePlayer(Long id, Player responsePlayer) {
        Player playerFromBD = getById(id);
        if (responsePlayer.getName()!=null) playerFromBD.setName(responsePlayer.getName());
        if (responsePlayer.getTitle() != null) playerFromBD.setTitle(responsePlayer.getTitle());
        if (responsePlayer.getRace() != null)playerFromBD.setRace(responsePlayer.getRace());
        if (responsePlayer.getProfession() != null)playerFromBD.setProfession(responsePlayer.getProfession());
        if (responsePlayer.getBirthday() != null)playerFromBD.setBirthday(responsePlayer.getBirthday());
        if (responsePlayer.getBanned() != null)playerFromBD.setBanned(responsePlayer.getBanned());
        if (responsePlayer.getExperience() != null) {
            playerFromBD.setExperience(responsePlayer.getExperience());
            playerFromBD.setLevel(playerLevel(responsePlayer.getExperience()));
            playerFromBD.setUntilNextLevel(untilNextLevel(responsePlayer.getExperience()));
        }
        if (!isPlayerValid(playerFromBD)) return null;
        playerRepository.saveAndFlush(playerFromBD);

        return playerFromBD;
    }

    @Override
    public void deletePlayer(Player player) {
        playerRepository.delete(player);

    }
    public boolean isAvailableFromDb(Long id){
        return playerRepository.existsById(id);
    }

    @Override
    public Player getById(Long id) {
        if (isIdValid(id)) {
            return playerRepository.findById(id).orElse(null);}
        return null;
    }

    public boolean isPlayerValid(Player player){
        final Calendar calendarBefore = new GregorianCalendar(3000, 0 , 1);
        final Calendar calendarAfter = new GregorianCalendar(2000, 0 , 1);
        Date before = calendarBefore.getTime();
        Date after = calendarAfter.getTime();

        if (
                player.getName() != null && player.getName().length() != 0 && player.getName().length() <=12
                && player.getTitle() != null && player.getTitle().length() <= 30
                && player.getRace() != null
                && player.getProfession() != null
                && player.getBirthday() != null && player.getBirthday().after(after) && player.getBirthday().before(before)
                && player.getExperience() != null && player.getExperience() > 0 && player.getExperience() <= 10000000
        ) {return true;}
        return false;
    }


    public Integer playerLevel(Integer experience){
        Double sqrtValue = Math.sqrt(2500 + 200*experience);
        double level = (sqrtValue - 50) / 100;
        return (int) level;

    }

    public Integer untilNextLevel(Integer experience){
        Integer playerLevel = playerLevel(experience);
        return 50 * (playerLevel +1) * (playerLevel + 2) - experience;
    }
    public static boolean isIdValid(Long id){
        double doubleId = (double) id;
        if (doubleId - id > 0) return false;
        return id > 0;

    }




}
