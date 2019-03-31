package application;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import entities.Candidacy;
import entities.CandidacyStatus;
import entities.Candidate;
import entities.JobOffer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import services_cand_interv.CandidacyServiceRemote;
import services_cand_interv.InterviewServiceRemote;
import services_cand_interv.JobOfferServiceRemote;

public class RecruitmentController implements Initializable {
	
	static int EntId=1;
	private Pane currentPane;
	@FXML
	private StackPane rootPane;
	@FXML
	private JFXButton btn_showOffers,btn_showTests,btn_showInterviews,btn_showCandidacies;
	@FXML
	private JFXButton btn_newOffer,btn_editOffer,btn_deleteOffer,btn_viewCand,btn_acceptCand,btn_rejectCand;
	@FXML
	private JFXButton btn_cancelAddOffer,btn_addOffer;
	@FXML
	private Pane pane_offers,pane_candidacies,pane_interviews,pane_tests,pane_addOffer,pane_viewCand;
	@FXML
	private JFXListView<Label>lv_offers;
	@FXML
	private JFXDatePicker dp_startDate,dp_endDate,dp_nOffStart,dp_nOffEnd;
	@FXML
	private JFXTextArea ta_description,ta_nOffDescription;
	@FXML
	private JFXTextField tf_nOffTitle;
	@FXML
	private Label lbl_offer;
	@FXML
	private TableView<Candidacy>tv_viewCand;
	@FXML
	private TableColumn<Candidacy,String>tc_nameCand,tc_emailCand,tc_birthCand;
	@FXML
	private TableColumn<Candidacy,Date>tc_submitCand;
	@FXML
	private TableColumn<Candidacy,CandidacyStatus>tc_statusCand;
	
	
	private String jndiname = "spectrum-ear/spectrum-ejb/JobOfferService!services_cand_interv."
			+ "JobOfferServiceRemote";
	private String jndiname2 ="spectrum-ear/spectrum-ejb/CandidacyService!services_cand_interv."
			+ "CandidacyServiceRemote";
	private String jndiname3 ="spectrum-ear/spectrum-ejb/InterviewService!services_cand_interv."
			+ "InterviewServiceRemote";
	
	
	public void getOffers() throws NamingException{
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		//CandidacyServiceRemote candidacy_s = (CandidacyServiceRemote) context.lookup(jndiname2);
		//InterviewServiceRemote interview_s= (InterviewServiceRemote) context.lookup(jndiname3);
		lv_offers.getItems().clear();
		for (JobOffer offer : offer_s.getjobOffersByEnt(EntId)) {
			Label lbl = new Label(offer.getTitle());
			lv_offers.getItems().add(lbl);
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane_offers.toFront();
		try {
			getOffers();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	@FXML
	public void menuButton(ActionEvent e) throws NamingException{
		if(currentPane==pane_addOffer) {
			cancelAddOffer(new ActionEvent());
			
		}
		else if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, finish the changes first", "Ok");
		}
		else if(e.getSource()==btn_showOffers) {
			pane_offers.toFront();
			getOffers();
			currentPane = pane_offers;
		}
		else if(e.getSource()==btn_showCandidacies) {
			currentPane = pane_candidacies;
			pane_candidacies.toFront();
		}
		else if(e.getSource()==btn_showInterviews) {
			currentPane = pane_interviews;
			pane_interviews.toFront();
		}
		else if(e.getSource()==btn_showTests) {
			pane_tests.toFront();
			currentPane = pane_tests;
		}
	}
	
	@FXML
	public void selectedOffer(MouseEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
		JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentOffer.getStart());
		dp_startDate.setValue(LocalDate.of(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH)));
		cal.setTime(currentOffer.getEnd());
		dp_endDate.setValue(LocalDate.of(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH)));
		ta_description.setText(currentOffer.getDescription());
	}
	
	@FXML
	public void editOffer(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		if (btn_editOffer.getText().equals("Edit")) {
			if (lv_offers.getSelectionModel().isEmpty())
				simpleDialog(rootPane, "Select the job offer you want to edit", "Ok");
			else {
				btn_editOffer.setText("Save");
				ta_description.setEditable(true);
				dp_startDate.setDisable(true);
			}
		}else {
			Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
			JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
			currentOffer.setDescription(ta_description.getText());
			currentOffer.setEnd(Date.from(dp_endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			offer_s.modifyJobOffer(EntId, currentOffer);
			btn_editOffer.setText("Edit");
			ta_description.setEditable(false);
			dp_startDate.setDisable(false);
		}
	}
	@FXML
	public void newOffer(ActionEvent e) throws NamingException {
		if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, finish the changes first", "Ok");
		}else {
			pane_addOffer.toFront();
			dp_nOffStart.setValue(LocalDate.now());
			dp_nOffEnd.setValue(LocalDate.now().plusDays(1));
			currentPane = pane_addOffer;
		}
	}
	@FXML
	public void cancelAddOffer(ActionEvent e) throws NamingException {
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXButton no = new JFXButton("No");
		JFXButton yes = new JFXButton("Yes");
		JFXDialog dialog = new JFXDialog(rootPane,dialogLayout,JFXDialog.DialogTransition.TOP);
		yes.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
			tf_nOffTitle.clear();
			ta_nOffDescription.clear();
			pane_offers.toFront();
			currentPane = pane_offers;
		});
		no.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
		});
		dialogLayout.setHeading(new Label("Modifications not saved, continue ?"));
		dialogLayout.setActions(no,yes);
		dialog.show();
	}
	public void simpleDialog(StackPane pane, String msg,String buttonMsg) {
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		JFXButton button = new JFXButton(buttonMsg);
		JFXDialog dialog = new JFXDialog(pane,dialogLayout,JFXDialog.DialogTransition.TOP);
		button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
			dialog.close();
		});
		dialogLayout.setHeading(new Label(msg));
		dialogLayout.setActions(button);
		dialog.show();
	}
	@FXML
	public void addOffer(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		Date end = Date.from(dp_nOffEnd.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		if(tf_nOffTitle.getText().equals("")||ta_nOffDescription.getText().equals("")) {
			simpleDialog(rootPane, "Required fields not filled", "Ok");
		}else if(end.compareTo(new Date())<=0) {
			simpleDialog(rootPane, "End date must be after today", "Ok");
		}else if(offer_s.getjobOfferByTitle(tf_nOffTitle.getText(), EntId)!=null) {
			simpleDialog(rootPane, "This job offer's title already exist", "Change it");
		}else {
			JobOffer offer = new JobOffer();
			offer.setTitle(tf_nOffTitle.getText());
			offer.setDescription(ta_nOffDescription.getText());
			offer.setStart(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			offer.setEnd(end);
			offer_s.addJobOffer(EntId, offer);
			pane_offers.toFront();
			tf_nOffTitle.clear();
			ta_nOffDescription.clear();
			currentPane = pane_offers;
			getOffers();
		}
	}
	@FXML
	public void removeOffer(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		if (lv_offers.getSelectionModel().isEmpty()) {
			simpleDialog(rootPane, "Select the job offer you want to delete", "Ok");
		}else if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, finish the changes first", "Ok");
		}else {
			Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
			JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
			JFXDialogLayout dialogLayout = new JFXDialogLayout();
			JFXButton no = new JFXButton("No");
			JFXButton delete = new JFXButton("Delete");
			JFXDialog dialog = new JFXDialog(rootPane,dialogLayout,JFXDialog.DialogTransition.TOP);
			delete.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
				offer_s.removeJobOfferFromEnt(EntId, currentOffer.getId());
				dialog.close();
				try {
					getOffers();
				} catch (NamingException e1) {
					e1.printStackTrace();
				}
			});
			no.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent)-> {
				dialog.close();
			});
			dialogLayout.setHeading(new Label("Do you really want to delete this job offer ?"));
			dialogLayout.setActions(no,delete);
			dialog.show();
		}
	}
		
	//----------------------------------CANDIDACIES PART---------------------------------------------------------------
	
	@FXML
	public void viewCand(ActionEvent e) throws NamingException{
		if (lv_offers.getSelectionModel().isEmpty()) {
			simpleDialog(rootPane, "Select the job offer", "Ok");
		}else if (btn_editOffer.getText().equals("Save")) {
			simpleDialog(rootPane, "You are in edit mode, finish the changes first", "Ok");
		}else {
			pane_viewCand.toFront();
			currentPane = pane_viewCand;
			loadCand(getCandidacies());
		}
	}
	
	public void loadCand(ObservableList<Candidacy> liste) throws NamingException {
		tv_viewCand.getItems().clear();
		tc_nameCand.setCellValueFactory(new PropertyValueFactory<>("name"));
		tc_birthCand.setCellValueFactory(new PropertyValueFactory<>("birth"));
		tc_emailCand.setCellValueFactory(new PropertyValueFactory<>("email"));
		tc_submitCand.setCellValueFactory(new PropertyValueFactory<>("date"));
		tc_statusCand.setCellValueFactory(new PropertyValueFactory<>("status"));
		tv_viewCand.setItems(liste);
	}
	
	public ObservableList<Candidacy> getCandidacies() throws NamingException{
		Context context = new InitialContext();
		JobOfferServiceRemote offer_s = (JobOfferServiceRemote) context.lookup(jndiname);
		CandidacyServiceRemote candidacy_s = (CandidacyServiceRemote) context.lookup(jndiname2);
		Label offerlbl = lv_offers.getSelectionModel().getSelectedItem();
		JobOffer currentOffer = offer_s.getjobOfferByTitle(offerlbl.getText(), EntId);
		lbl_offer.setText(currentOffer.getTitle());
		ObservableList<Candidacy>candidacies = FXCollections.observableArrayList();
		for (Candidacy candidacy : candidacy_s.getCandidaciesByOffer(currentOffer.getId())) {
			Candidate candidate = candidacy.getCandidate();
			candidacy.setName(candidate.getUser().getName());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			candidacy.setBirth(dateFormat.format(candidate.getUser().getBirth()));
			candidacy.setEmail(candidate.getUser().getEmail());
			candidacies.add(candidacy);
		}
		return candidacies;
	}
	
	@FXML
	public void acceptCand(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		CandidacyServiceRemote candidacy_s = (CandidacyServiceRemote) context.lookup(jndiname2);
		String name = tv_viewCand.getSelectionModel().getSelectedItem().getName();
		Candidacy cand = new Candidacy();
		for (Candidacy candidacy : getCandidacies()) {
			if(candidacy.getName().equals(name)) {
				cand = candidacy;
			}
		}
		cand.setStatus(CandidacyStatus.accepted);
		candidacy_s.modifyCandidacy(cand);
		loadCand(getCandidacies());
	}
	@FXML
	public void rejectCand(ActionEvent e) throws NamingException {
		Context context = new InitialContext();
		CandidacyServiceRemote candidacy_s = (CandidacyServiceRemote) context.lookup(jndiname2);
		String name = tv_viewCand.getSelectionModel().getSelectedItem().getName();
		Candidacy cand = new Candidacy();
		for (Candidacy candidacy : getCandidacies()) {
			if(candidacy.getName().equals(name)) {
				cand = candidacy;
			}
		}
		cand.setStatus(CandidacyStatus.rejected);
		candidacy_s.modifyCandidacy(cand);
		loadCand(getCandidacies());
	}
	@FXML
	public void onSelectedRow(MouseEvent e) throws NamingException {
		if(tv_viewCand.getSelectionModel().getSelectedItem().getStatus()!=CandidacyStatus.pending) {
			btn_acceptCand.setDisable(true);
			btn_rejectCand.setDisable(true);
		}else {
			btn_acceptCand.setDisable(false);
			btn_rejectCand.setDisable(false);
		}
	}
	
}
