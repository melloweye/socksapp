package ru.socks.registry.socksapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.socks.registry.socksapp.models.Socks;

public interface SocksRepository extends JpaRepository<Socks, Long>, JpaSpecificationExecutor<Socks> {
}
