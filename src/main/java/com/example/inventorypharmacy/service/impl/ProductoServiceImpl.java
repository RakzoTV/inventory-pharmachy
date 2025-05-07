package com.example.inventorypharmacy.service.impl;


import com.example.inventorypharmacy.dto.ProductoDTO;
import com.example.inventorypharmacy.dto.ProductoResponseDTO;
import com.example.inventorypharmacy.dto.ProductoSucursalResponseDTO;
import com.example.inventorypharmacy.model.*;
import com.example.inventorypharmacy.repository.*;
import com.example.inventorypharmacy.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private UnidadRepository unidadRepo;

    @Autowired
    private ProveedorRepository proveedorRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Autowired
    private PrecioRepository precioRepo;

    @Autowired
    private ProductoSucursalRepository productoSucursalRepo;

    private ProductoDTO toDTO(Producto p) {
        return new ProductoDTO(
                p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getStock(),
                p.getUnidad().getIdUnidad(),
                p.getProveedor().getIdProveedor(),
                p.getCategoria().getIdCategoria()
        );
    }

    private Producto toEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setIdProducto(dto.getIdProducto());
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setStock(dto.getStock());
        p.setUnidad(unidadRepo.findById(dto.getIdUnidad()).orElseThrow());
        p.setProveedor(proveedorRepo.findById(dto.getIdProveedor()).orElseThrow());
        p.setCategoria(categoriaRepo.findById(dto.getIdCategoria()).orElseThrow());
        return p;
    }

    @Override
    public List<ProductoDTO> listarTodos() {
        return productoRepo.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public ProductoDTO buscarPorId(Long id) {
        return productoRepo.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public ProductoDTO guardar(ProductoDTO dto) {
        Producto saved = productoRepo.save(toEntity(dto));
        return toDTO(saved);
    }

    @Override
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        if (!productoRepo.existsById(id)) return null;
        dto.setIdProducto(id);
        Producto updated = productoRepo.save(toEntity(dto));
        return toDTO(updated);
    }

    @Override
    public void eliminar(Long id) {
        productoRepo.deleteById(id);
    }



    @Override
    public List<ProductoResponseDTO> obtenerProductosConDetalle() {
        List<Producto> productos = productoRepo.findAll();
        List<ProductoResponseDTO> resultado = new ArrayList<>();

        for (Producto producto : productos) {
            Double precioActual = precioRepo
                    .findPrecioActualByProductoId(producto.getIdProducto())
                    .stream()
                    .findFirst()
                    .map(Precio::getPrecio_unitario)
                    .orElse(null);

            ProductoResponseDTO dto = new ProductoResponseDTO(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getStock(),
                    producto.getUnidad() != null ? producto.getUnidad().getDescripcion() : null,
                    producto.getProveedor() != null ? producto.getProveedor().getNombre() : null,
                    producto.getCategoria() != null ? producto.getCategoria().getNombre() : null,
                    precioActual
            );
            resultado.add(dto);
        }

        return resultado;
    }
    public List<ProductoSucursalResponseDTO> obtenerProductosPorSucursal(Long idSucursal) {
        List<ProductoSucursal> lista = productoSucursalRepo.findBySucursal_IdSucursal(idSucursal);

        return lista.stream().map(ps -> {
            Producto p = ps.getProducto();
            Double precio = precioRepo
                    .findPrecioActualByProductoId(p.getIdProducto())
                    .stream()
                    .findFirst()
                    .map(precioObj -> precioObj.getPrecio_unitario())
                    .orElse(null);

            return new ProductoSucursalResponseDTO(
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getDescripcion(),
                    ps.getStock(),
                    p.getUnidad().getDescripcion(),
                    p.getProveedor().getNombre(),
                    p.getCategoria().getNombre(),
                    precio,
                    ps.getSucursal().getNombre()
            );
        }).toList();
    }

}
