package tadeas_musil.ticketing_system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.TicketCategory;
import tadeas_musil.ticketing_system.repository.TicketCategoryRepository;
import tadeas_musil.ticketing_system.service.TicketCategoryService;

@Service
public class TicketCategoryServiceImpl implements TicketCategoryService {

    @Autowired
    private TicketCategoryRepository categoryRepository;

    @Override
    public List<TicketCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
