package ma.enset.patientmvc.repositories.web;

import lombok.AllArgsConstructor;
import ma.enset.patientmvc.entities.Patient;
import ma.enset.patientmvc.repositories.PatientRepository;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    @GetMapping(path="/index")
public String patients(Model model,@RequestParam(value = "size",defaultValue = "5") int size,@RequestParam(value = "page",defaultValue = "0")int page,@RequestParam(value = "keyword",defaultValue = "") String keyword){
        Page<Patient> pagePatients=patientRepository.findByNomContains(keyword,PageRequest.of(page,size));
        model.addAttribute("listPatient",pagePatients.getContent());
        model.addAttribute("pages",new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentpage",page);
        model.addAttribute("keyword",keyword);
        return "patients";


}
    @GetMapping(path="/delete")
    public String delete(Long id,String keyword,int page){

        patientRepository.deleteById(id);

        return "redirect:/index?page="+page+"&keyword="+keyword;
    }
    @GetMapping(path="/formPatient")
    public String formPatient(Model model){
        model.addAttribute("patient",new Patient());

   return "formPatient";
    }

    @PostMapping(path ="/save" )
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult){
        //bindingresult stokage la collection de erreurs
        if (bindingResult.hasErrors())return "formPatient";
        patientRepository.save(patient);

        return "redirect:/formPatient";
    }

    @GetMapping(path="/editPatient")
    public String editPatient(Model model, Long id){
        Patient patient=patientRepository.findById(id).orElse(null);
        if (patient==null)throw new RuntimeException("patient introuvable");
        //on stoke dans le model
        model.addAttribute("patient",new Patient());

        return "editPatient";
    }


}
