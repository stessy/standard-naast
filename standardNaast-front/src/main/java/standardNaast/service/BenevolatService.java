/**
 * 
 */
package standardNaast.service;

import javax.inject.Named;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author stessy
 * 
 */
@Named
@Service("benevolatService")
@Transactional(readOnly = true)
public class BenevolatService {

}
