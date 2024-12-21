package ru.socks.registry.socksapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.socks.registry.socksapp.exceptions.SocksNotFoundException;
import ru.socks.registry.socksapp.models.Socks;
import ru.socks.registry.socksapp.repositories.SocksRepository;
import ru.socks.registry.socksapp.specifications.SocksSpecifications;
import ru.socks.registry.socksapp.utils.QuantityFilterType;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocksServiceImpl implements SocksService {

    private final SocksRepository socksRepository;

    @Override
    public Socks createSocks(Socks socks) {
        return socksRepository.save(
                Socks.builder()
                        .color(socks.getColor())
                        .cottonPart(socks.getCottonPart())
                        .quantity(socks.getQuantity())
                        .build()
        );
    }

    @Override
    public List<Socks> getAllSocks() {
        return socksRepository.findAll();
    }

    @Override
    public void saveAll(List<Socks> socks) {
        socksRepository.saveAll(socks);
    }

    @Override
    public Socks getSocksById(long id) {
        Socks socks = socksRepository.findById(id).orElseThrow(() -> new SocksNotFoundException("Socks with id = " + id + " not found"));
        return socks;
    }

    @Override
    public List<Socks> getFilteredSocks(String color, Integer quantity, QuantityFilterType filterType) {
        Specification<Socks> specification = Specification.where(null);

        if (color != null) {
            specification = specification.and(SocksSpecifications.byColor(color));
        }

        if (quantity != null && filterType != null) {
            switch (filterType) {
                case MORE_THAN -> specification = specification.and(SocksSpecifications.moreThan(quantity));
                case LESS_THAN -> specification = specification.and(SocksSpecifications.lessThan(quantity));
                case EQUAL -> specification = specification.and(SocksSpecifications.equal(quantity));
            }
        }

        return socksRepository.findAll(specification);
    }

    @Override
    public Socks updateSocks(Long id, Socks newData) {
        Socks socks = socksRepository.findById(id).orElseThrow(() -> new SocksNotFoundException("Socks with id = " + id + " not found"));

        socks.setQuantity(newData.getQuantity());
        socks.setCottonPart(newData.getCottonPart());
        socks.setColor(newData.getColor());

        socksRepository.save(socks);
        return socks;
    }

    @Override
    public void deleteSocksById(long id) {
        socksRepository.findById(id).orElseThrow(() -> new SocksNotFoundException("Socks with id = " + id + " not found"));

        if (socksRepository.existsById(id)) {
            socksRepository.deleteById(id);
        }
    }
}
