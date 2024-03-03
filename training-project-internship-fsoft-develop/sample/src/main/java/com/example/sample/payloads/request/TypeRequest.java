package com.example.sample.payloads.request;

import com.example.sample.entities.Book;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Data
@Getter
@Setter
public class TypeRequest {
    private Long id;
    private String name;
    private String description;
    private Set<Book> books = new HashSet<>();
}
