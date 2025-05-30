package com.example.inventorypharmacy.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoSucursalResponseDTO {
    private Long idProducto;
    private String nombre;
    private String descripcion;
    private Integer stock;
    private String unidad;
    private String proveedor;
    private String categoria;
    private Double precio;
    private String sucursal;
}