package ru.socks.registry.socksapp.services;

import ru.socks.registry.socksapp.models.Socks;
import ru.socks.registry.socksapp.utils.QuantityFilterType;

import java.util.List;

public interface SocksService {
    Socks createSocks(Socks socks);

    List<Socks> getAllSocks();
    
    Socks getSocksById(long id);

    public void saveAll(List<Socks> socks);

    public List<Socks> getFilteredSocks(String color, Integer quantity, QuantityFilterType filterType);

    Socks updateSocks(Long id, Socks newData);

    void deleteSocksById(long id);
}
