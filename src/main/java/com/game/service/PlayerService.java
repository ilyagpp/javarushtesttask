package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

/*

1. получать список всех зарегистрированных игроков;
2. создавать нового игрока;
3. редактировать характеристики существующего игрока;
4. удалять игрока;
5. получать игрока по id;
6. получать отфильтрованный список игроков в соответствии с переданными фильтрами;
7. получать количество игроков, которые соответствуют фильтрам.
*/

    List<Player> getPlayers();
    Integer getCountPlayers(List<Player> playerList);
    Player createPlayer(Player player);
    Player updatePlayer(Long id, Player responsePlayer);
    void deletePlayer(Player player);
    public boolean isAvailableFromDb(Long id);
    Player getById(Long id);

    List<Player> sortPlayersList(List<Player> players, PlayerOrder order);
    List<Player> getFilteredPlayers(                       String name,
                                                           String title,
                                                           Race race,
                                                           Profession profession,
                                                           Long after,
                                                           Long before,
                                                           Boolean banned,
                                                           Integer minExperience,
                                                           Integer maxExperience,
                                                           Integer minLevel,
                                                           Integer maxLevel);

    List<Player> getPage(List<Player> players, Integer pageNumber, Integer pageSize);
    //List<Player> getSortedPlayers();
   // Integer countOfGetSortedPlayers();
    boolean isPlayerValid(Player player);

}
