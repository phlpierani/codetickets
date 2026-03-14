package br.com.alura.demo;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ImportacaoProcessor implements ItemProcessor<Importacao, Importacao> {

    @Override
    public Importacao process(Importacao item) throws Exception {
        if (item.getTipoIngresso().equalsIgnoreCase("vip")){
            item.setTaxaAdm(130.0);
        } else if (item.getTipoIngresso().equalsIgnoreCase("camarote")){
            item.setTaxaAdm(80.0);
        } else {
            item.setTaxaAdm(50.0);
        }

        return item;
    }
}
