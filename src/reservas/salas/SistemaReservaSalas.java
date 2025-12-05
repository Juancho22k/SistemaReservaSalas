//Proyecto hecho para netbeans 8.2 
//Juan Centeno
package reservas.salas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import javafx.beans.property.ObjectProperty;

public class SistemaReservaSalas extends Application {
    
    // Datos del sistema
    private ArrayList<Sala> listaSalas = new ArrayList<>();
    private ArrayList<Reserva> listaReservas = new ArrayList<>();
    
    // Componentes de la interfaz
    private TableView<Sala> tablaSalas;
    private TableView<Reserva> tablaReservas;
    private TextField campoNombreSala, campoCapacidad, campoResponsable, campoMotivo;
    private ComboBox<String> selectorSalas;
    private DatePicker selectorFecha;
    private ComboBox<String> selectorHoraInicio, selectorHoraFin;
    
    @Override
    public void start(Stage ventanaPrincipal) {
        cargarDatosIniciales();
        
        ventanaPrincipal.setTitle("Sistema de Reserva de Salas - UNEG");
        
        // Crear pestañas
        TabPane panelPestanas = new TabPane();
        
        // Pestaña de Salas
        Tab pestanaSalas = new Tab("Gestión de Salas");
        pestanaSalas.setContent(crearPanelSalas());
        pestanaSalas.setClosable(false);
        
        // Pestaña de Reservas
        Tab pestanaReservas = new Tab("Reservas");
        pestanaReservas.setContent(crearPanelReservas());
        pestanaReservas.setClosable(false);
        
        // Pestaña de Consultas
        Tab pestanaConsultas = new Tab("Consultas");
        pestanaConsultas.setContent(crearPanelConsultas());
        pestanaConsultas.setClosable(false);
        
        panelPestanas.getTabs().addAll(pestanaSalas, pestanaReservas, pestanaConsultas);
        
        Scene escena = new Scene(panelPestanas, 800, 600);
        ventanaPrincipal.setScene(escena);
        ventanaPrincipal.show();
    }
    
    private void cargarDatosIniciales() {
        // Agregar algunas salas de ejemplo
        listaSalas.add(new Sala(1, "Sala de Conferencias A", 50));
        listaSalas.add(new Sala(2, "Sala de Reuniones B", 20));
        listaSalas.add(new Sala(3, "Laboratorio de Computación", 30));
        listaSalas.add(new Sala(4, "Aula Magna", 100));
        
        // Inicializar combos de horas
        ObservableList<String> horasDisponibles = FXCollections.observableArrayList();
        for (int hora = 8; hora <= 20; hora++) {
            horasDisponibles.add(String.format("%02d:00", hora));
            horasDisponibles.add(String.format("%02d:30", hora));
        }
        selectorHoraInicio = new ComboBox<>(horasDisponibles);
        selectorHoraFin = new ComboBox<>(horasDisponibles);
    }
    
    private VBox crearPanelSalas() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #f4f4f4;");
        
        // Título
        Label titulo = new Label("GESTIÓN DE SALAS");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Formulario para agregar salas
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));
        formulario.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        
        campoNombreSala = new TextField();
        campoCapacidad = new TextField();
        
        formulario.add(new Label("Nombre de la Sala:"), 0, 0);
        formulario.add(campoNombreSala, 1, 0);
        formulario.add(new Label("Capacidad:"), 0, 1);
        formulario.add(campoCapacidad, 1, 1);
        
        Button botonAgregar = new Button("Agregar Sala");
        botonAgregar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        botonAgregar.setOnAction(e -> agregarSala());
        
        // Tabla de salas
        tablaSalas = new TableView<>();
        
        TableColumn<Sala, Integer> columnaNumero = new TableColumn<>("Número");
        columnaNumero.setCellValueFactory(cellData -> cellData.getValue().numeroProperty());
        /**/
         
        TableColumn<Sala, String> columnaNombre = new TableColumn<>("Nombre");
        columnaNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        /**/
         TableColumn<Sala, Integer> columnaCapacidad = new TableColumn<>("Capacidad");
         columnaCapacidad.setCellValueFactory(cellData -> cellData.getValue().capacidadProperty());
         /**/
        TableColumn<Sala, String> columnaEstado = new TableColumn<>("Estado");
        columnaEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        
        tablaSalas.getColumns().addAll(columnaNumero, columnaNombre, columnaCapacidad, columnaEstado);
        actualizarTablaSalas();
        
        // Botón eliminar
        Button botonEliminar = new Button("Eliminar Sala Seleccionada");
        botonEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        botonEliminar.setOnAction(e -> eliminarSala());
        
        panel.getChildren().addAll(titulo, formulario, botonAgregar, new Label("Lista de Salas:"), tablaSalas, botonEliminar);
        
        return panel;
    }
    
    private VBox crearPanelReservas() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #f4f4f4;");
        
        Label titulo = new Label("RESERVA DE SALAS");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Formulario de reserva
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));
        formulario.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        
        selectorSalas = new ComboBox<>();
        campoResponsable = new TextField();
        campoMotivo = new TextField();
        selectorFecha = new DatePicker();
        
        formulario.add(new Label("Sala:"), 0, 0);
        formulario.add(selectorSalas, 1, 0);
        formulario.add(new Label("Fecha:"), 0, 1);
        formulario.add(selectorFecha, 1, 1);
        formulario.add(new Label("Hora Inicio:"), 0, 2);
        formulario.add(selectorHoraInicio, 1, 2);
        formulario.add(new Label("Hora Fin:"), 0, 3);
        formulario.add(selectorHoraFin, 1, 3);
        formulario.add(new Label("Responsable:"), 0, 4);
        formulario.add(campoResponsable, 1, 4);
        formulario.add(new Label("Motivo:"), 0, 5);
        formulario.add(campoMotivo, 1, 5);
        
        Button botonReservar = new Button("Realizar Reserva");
        botonReservar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        botonReservar.setOnAction(e -> realizarReserva());
        
        // Actualizar combo de salas
        actualizarSelectorSalas();
        
        panel.getChildren().addAll(titulo, formulario, botonReservar);
        
        return panel;
    }
    
    private VBox crearPanelConsultas() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #f4f4f4;");
        
        Label titulo = new Label("CONSULTAS Y REPORTES");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Tabla de reservas
   tablaReservas = new TableView<>();

    TableColumn<Reserva, String> columnaSala = new TableColumn<>("Sala");
     columnaSala.setCellValueFactory(cellData -> cellData.getValue().nombreSalaProperty());
      columnaSala.setPrefWidth(180); // Aumenta ancho de Sala
       columnaSala.setStyle("-fx-alignment: CENTER;");
       
    TableColumn<Reserva, String> columnaFecha = new TableColumn<>("Fecha");
     columnaFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
      columnaFecha.setStyle("-fx-alignment: CENTER;");
    TableColumn<Reserva, String> columnaHorario = new TableColumn<>("Horario");
     columnaHorario.setCellValueFactory(cellData -> cellData.getValue().horarioProperty());

    TableColumn<Reserva, String> columnaResponsable = new TableColumn<>("Responsable");
     columnaResponsable.setCellValueFactory(cellData -> cellData.getValue().responsableProperty());
      columnaResponsable.setPrefWidth(160); // Aumenta ancho de Responsable
       columnaResponsable.setStyle("-fx-alignment: CENTER;");
       
    TableColumn<Reserva, String> columnaMotivo = new TableColumn<>("Motivo");
     columnaMotivo.setCellValueFactory(cellData -> cellData.getValue().motivoProperty());
      columnaMotivo.setPrefWidth(240); // Aumenta ancho de Motivo
       columnaMotivo.setStyle("-fx-alignment: CENTER;");
       
tablaReservas.getColumns().addAll(
    columnaSala, columnaFecha, columnaHorario, columnaResponsable, columnaMotivo
);

actualizarTablaReservas();
        // Botones de acciones
        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER);
        
        Button botonCancelar = new Button("Cancelar Reserva");
        botonCancelar.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        botonCancelar.setOnAction(e -> cancelarReserva());
        
        Button botonActualizar = new Button("Actualizar Lista");
        botonActualizar.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        botonActualizar.setOnAction(e -> actualizarTablaReservas());
        
        panelBotones.getChildren().addAll(botonCancelar, botonActualizar);
        
        panel.getChildren().addAll(titulo, new Label("Reservas Activas:"), tablaReservas, panelBotones);
        
        return panel;
    }
    
    private void agregarSala() {
        try {
            String nombreSala = campoNombreSala.getText();
            int capacidadSala = Integer.parseInt(campoCapacidad.getText());
            
            if (nombreSala.isEmpty()) {
                mostrarMensaje("Error", "El nombre de la sala es requerido");
                return;
            }
            
            if (capacidadSala <= 0) {
                mostrarMensaje("Error", "La capacidad debe ser mayor a 0");
                return;
            }
            
            int nuevoNumeroSala = listaSalas.size() + 1;
            Sala nuevaSala = new Sala(nuevoNumeroSala, nombreSala, capacidadSala);
            listaSalas.add(nuevaSala);
            
            actualizarTablaSalas();
            actualizarSelectorSalas();
            
            campoNombreSala.clear();
            campoCapacidad.clear();
            
            mostrarMensaje("Éxito", "Sala agregada correctamente");
            
        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La capacidad debe ser un número válido");
        }
    }
    
    private void eliminarSala() {
        Sala salaSeleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (salaSeleccionada != null) {
            listaSalas.remove(salaSeleccionada);
            actualizarTablaSalas();
            actualizarSelectorSalas();
            mostrarMensaje("Éxito", "Sala eliminada correctamente");
        } else {
            mostrarMensaje("Error", "Seleccione una sala para eliminar");
        }
    }
    
    private void realizarReserva() {
        try {
            Sala salaSeleccionada = null;
            String nombreSalaSeleccionada = selectorSalas.getValue();
            
            // Buscar sala seleccionada
            for (Sala sala : listaSalas) {
                if (sala.getNombre().equals(nombreSalaSeleccionada)) {
                    salaSeleccionada = sala;
                    break;
                }
            }
            
            if (salaSeleccionada == null) {
                mostrarMensaje("Error", "Seleccione una sala válida");
                return;
            }
            
            if (selectorFecha.getValue() == null) {
                mostrarMensaje("Error", "Seleccione una fecha");
                return;
            }
            
            if (selectorHoraInicio.getValue() == null || selectorHoraFin.getValue() == null) {
                mostrarMensaje("Error", "Seleccione horario de inicio y fin");
                return;
            }
            
            if (campoResponsable.getText().isEmpty()) {
                mostrarMensaje("Error", "El nombre del responsable es requerido");
                return;
            }
            
            // Validar conflicto de horarios
            if (existeConflictoHorario(salaSeleccionada, selectorFecha.getValue().toString(), 
                                    selectorHoraInicio.getValue(), selectorHoraFin.getValue())) {
                mostrarMensaje("Error", "La sala ya está reservada en ese horario");
                return;
            }
            
          
            Reserva nuevaReserva = new Reserva(
                
                salaSeleccionada.getNumero(),
                salaSeleccionada.getNombre(),
                selectorFecha.getValue().toString(),
                selectorHoraInicio.getValue() + " - " + selectorHoraFin.getValue(),
                campoResponsable.getText(),
                campoMotivo.getText()
            );
            
            listaReservas.add(nuevaReserva);
            actualizarTablaReservas();
            
            // Limpiar formulario
            campoResponsable.clear();
            campoMotivo.clear();
            
            mostrarMensaje("Éxito", "Reserva realizada correctamente");
            
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al realizar la reserva: " + e.getMessage());
        }
    }
    
    private void cancelarReserva() {
        Reserva reservaSeleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (reservaSeleccionada != null) {
            listaReservas.remove(reservaSeleccionada);
            actualizarTablaReservas();
            mostrarMensaje("Éxito", "Reserva cancelada correctamente");
        } else {
            mostrarMensaje("Error", "Seleccione una reserva para cancelar");
        }
    }
    
    private boolean existeConflictoHorario(Sala sala, String fechaReserva, String horaInicio, String horaFin) {
        for (Reserva reserva : listaReservas) {
            if (reserva.getNumeroSala() == sala.getNumero() && 
                reserva.getFecha().equals(fechaReserva)) {
                // Validar superposición de horarios 
                String[] horariosReserva = reserva.getHorario().split(" - ");
                if (horariosReserva.length == 2) {
                    String inicioExistente = horariosReserva[0];
                    String finExistente = horariosReserva[1];
                    
                    // Validación simple de superposición
                    if (!(horaFin.compareTo(inicioExistente) <= 0 || 
                          horaInicio.compareTo(finExistente) >= 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void actualizarTablaSalas() {
        ObservableList<Sala> datosTabla = FXCollections.observableArrayList(listaSalas);
        tablaSalas.setItems(datosTabla);
    }
    
    private void actualizarTablaReservas() {
        ObservableList<Reserva> datosTabla = FXCollections.observableArrayList(listaReservas);
        tablaReservas.setItems(datosTabla);
    }
    
    private void actualizarSelectorSalas() {
        ObservableList<String> nombresSalas = FXCollections.observableArrayList();
        for (Sala sala : listaSalas) {
            nombresSalas.add(sala.getNombre());
        }
        selectorSalas.setItems(nombresSalas);
    }
    
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

// Clase Sala
class Sala {
    private final int numero;
    private final String nombre;
    private final int capacidad;
    private boolean disponible;
    
    public Sala(int numero, String nombre, int capacidad) {
        this.numero = numero;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.disponible = true;
    }
    
    // Getters
    public int getNumero() { return numero; }
    public String getNombre() { return nombre; }
    public int getCapacidad() { return capacidad; }
    public boolean isDisponible() { return disponible; }
    
    
    public ObjectProperty<Integer> numeroProperty() {
        return new javafx.beans.property.SimpleIntegerProperty(numero).asObject();
    }
    
    public javafx.beans.property.StringProperty nombreProperty() {
        return new javafx.beans.property.SimpleStringProperty(nombre);
    }
    
    public ObjectProperty<Integer> capacidadProperty() {
        return new javafx.beans.property.SimpleIntegerProperty(capacidad).asObject();
    }
    
    public javafx.beans.property.StringProperty estadoProperty() {
        String estado = disponible ? "Disponible" : "Ocupada";
        return new javafx.beans.property.SimpleStringProperty(estado);
    }
}

// Clase Reserva
class Reserva {
    
    private final int numeroSala;
    private final String nombreSala;
    private final String fecha;
    private final String horario;
    private final String responsable;
    private final String motivo;
    
    public Reserva(int numeroSala, String nombreSala, String fecha, 
                   String horario, String responsable, String motivo) {
        this.numeroSala = numeroSala;
        this.nombreSala = nombreSala;
        this.fecha = fecha;
        this.horario = horario;
        this.responsable = responsable;
        this.motivo = motivo;
    }
    
    // Getters
    public int getNumeroSala() { return numeroSala; }
    public String getNombreSala() { return nombreSala; }
    public String getFecha() { return fecha; }
    public String getHorario() { return horario; }
    public String getResponsable() { return responsable; }
    public String getMotivo() { return motivo; }
    
     
    
    public javafx.beans.property.StringProperty nombreSalaProperty() {
        return new javafx.beans.property.SimpleStringProperty(nombreSala);
    }
    
    public javafx.beans.property.StringProperty fechaProperty() {
        return new javafx.beans.property.SimpleStringProperty(fecha);
    }
    
    public javafx.beans.property.StringProperty horarioProperty() {
        return new javafx.beans.property.SimpleStringProperty(horario);
    }
    
    public javafx.beans.property.StringProperty responsableProperty() {
        return new javafx.beans.property.SimpleStringProperty(responsable);
    }
    
    public javafx.beans.property.StringProperty motivoProperty() {
        return new javafx.beans.property.SimpleStringProperty(motivo);
    }
}