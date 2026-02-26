package domain.merchant;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public class Merchant {
    private final UUID id;
    private final String name;
    private final String countryCode;


    public Merchant(String name, String country1) {
        if( name==null || name.isBlank())
            throw new IllegalArgumentException("Name must be non-empty");
        if(country1==null || country1.isBlank())
            throw new IllegalArgumentException("Country code must me non-empty");

        //user may enter countryCode in lowercase so we convert into upperCase
        String normalized = country1.toUpperCase(Locale.ROOT);

        if (!Arrays.asList(Locale.getISOCountries()).contains(normalized))
            throw new IllegalArgumentException("Invalid Country Code");

        this.countryCode = normalized;
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public String getCountry() {
        return countryCode;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}
