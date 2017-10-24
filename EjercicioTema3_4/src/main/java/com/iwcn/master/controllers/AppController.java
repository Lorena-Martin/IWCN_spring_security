package com.iwcn.master.controllers;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iwcn.master.entities.Producto;
import com.iwcn.master.entities.Usuario;
import com.iwcn.master.repositories.ProductoRepository;
import com.iwcn.master.repositories.UsuarioRepository;

@Controller
public class AppController {
	
	@Autowired
	private ProductoRepository productoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@PostConstruct
	private void initDatabase() {
		
		// User #1: "user", with password "user1" and role "USER"
		GrantedAuthority[] userRoles = { new SimpleGrantedAuthority("ROLE_USER") };  
		usuarioRepository.save(new Usuario("user", "user", Arrays.asList(userRoles)));

		// User #2: "root", with password "root1" and roles "USER" and "ADMIN"  
		GrantedAuthority[] adminRoles = { new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN") };
		usuarioRepository.save(new Usuario("root", "root", Arrays.asList(adminRoles)));

	}
	
	@RequestMapping("/")
	public ModelAndView indexSinLoguear(Model model) {
		return new ModelAndView("indexSinLoguear_template");
	}
	
	@RequestMapping("/loguear")
	public ModelAndView loguear(Model model) {
		return new ModelAndView("loguear_template");
	}
	
	@RequestMapping("/cerrarSesion")
	public ModelAndView cerrarSesion(Model model) {
		return new ModelAndView("cerrarSesion_template");
	}
	
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
	@RequestMapping("/inicio")
	public ModelAndView index(Model model) {
		return new ModelAndView("index_template");
	}
	
	@Secured({"ROLE_ADMIN"})
    @RequestMapping("/nuevoProducto")
    public ModelAndView nuevoProducto(Model model, Producto prod) {
    	model.addAttribute(prod);
        return new ModelAndView("nuevoProducto_template");
    }
    
	@Secured({"ROLE_ADMIN"})
    @RequestMapping("/guardado")
    public ModelAndView guardado(Model model, Producto prod) {
    	productoRepository.save(prod);
    	return new ModelAndView("guardado_template").addObject("nombre", prod.getNombre());
    }
    
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping("/listaProductos")
    public ModelAndView listaProductos() {
    	return new ModelAndView("listaProductos_template").addObject("lista", productoRepository.findAll());
    }
    
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping("/info/{option}")
    public ModelAndView info(@PathVariable String option) {
    	int indice = Integer.parseInt(option);
    	Producto pr = productoRepository.findById(indice);
    	return new ModelAndView("info_template").addObject("codigo", pr.getCodigo()).addObject("nombre", pr.getNombre()).addObject("descripcion", pr.getDescripcion()).addObject("precio", pr.getPrecio());
    }
    
    @Secured({"ROLE_ADMIN"})
    @RequestMapping("/eliminado/{option}")
    public ModelAndView eliminado(@PathVariable String option) {
    	int indice = Integer.parseInt(option);
    	Producto pr = productoRepository.findById(indice);
    	productoRepository.delete(pr);
    	return new ModelAndView("eliminado_template").addObject("nombre", pr.getNombre());
    }
    
    @Secured({"ROLE_ADMIN"})
    @RequestMapping("/editar/{option}")
    public ModelAndView editar(Model model, @PathVariable String option) {
    	int indice = Integer.parseInt(option);
    	Producto pr = productoRepository.findById(indice);
    	model.addAttribute(pr);
    	return new ModelAndView("editar_template").addObject("id", pr.getId());
    }
    
    @Secured({"ROLE_ADMIN"})
    @RequestMapping("/editado")
    public ModelAndView editado(Producto prod) {
    	productoRepository.save(prod);
    	return new ModelAndView("editado_template").addObject("nombre", prod.getNombre());
    }
    

}
