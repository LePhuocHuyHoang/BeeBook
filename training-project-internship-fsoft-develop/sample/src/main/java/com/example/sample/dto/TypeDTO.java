package com.example.sample.dto;

import com.example.sample.entities.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeDTO {
    private Long id;
    private String name;
    private String description;
    private List<Book> books = new ArrayList<>();
}
