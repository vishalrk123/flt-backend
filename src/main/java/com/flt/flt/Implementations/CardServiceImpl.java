package com.flt.flt.Implementations;

import com.flt.flt.Services.CardService;
import com.flt.flt.dao.QuizCardRepository;
import com.flt.flt.models.Categories;
import com.flt.flt.models.Difficulties;
import com.flt.flt.models.QuizCard;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private QuizCardRepository quizCardRepository;

    @Override
    public QuizCard saveCard(QuizCard card) {
        long maxCardOrder = quizCardRepository.findMaxCardOrder();
        card.setCardOrder(maxCardOrder+1);
        return quizCardRepository.save(card);
    }

    @Transactional
    @Override
    public void deleteCard(long id) {
        Optional<QuizCard> quizCardOptional = quizCardRepository.findById(id);
        if (quizCardOptional.isPresent()) {
            QuizCard quizCard = quizCardOptional.get();
            long cardOrder = quizCard.getCardOrder();
            quizCardRepository.deleteById(id);
            quizCardRepository.updateCardOrderAfterDeletion(cardOrder);
        }
    }

    @Override
    public QuizCard updateCard(QuizCard card) {
        var oldCard = quizCardRepository.findById(card.getId()).orElseThrow();
        oldCard.setAnswer(card.getAnswer());
        oldCard.setCategory(card.getCategory());
        oldCard.setQuestion(card.getQuestion());
        oldCard.setDifficulty(card.getDifficulty());

        return quizCardRepository.save(oldCard);
    }

    @Override
    public Optional<QuizCard> findCardByCardOrder(long cardOrder) {
        Optional<QuizCard> card = quizCardRepository.findByCardOrder(cardOrder);
        return card;
    }

    @Override
    public long findMaxCardNumber() {
        return quizCardRepository.findMaxCardOrder();
    }

    @Override
    public List<QuizCard> getAllCards() {
        return quizCardRepository.findAllCards();
    }

    @Override
    public List<QuizCard> getFilteredCards(Difficulties difficulty, Categories category) {
        if(difficulty != Difficulties.ALL && category != Categories.ALL) {
            return quizCardRepository.findByDifficultyAndCategory(difficulty, category);
        }
        else if(difficulty != Difficulties.ALL) {
            return quizCardRepository.findByDifficulty(difficulty);
        }
        else if(category != Categories.ALL) {
            return quizCardRepository.findByCategory(category);
        }
        else return quizCardRepository.findAllCards();
    }
}
