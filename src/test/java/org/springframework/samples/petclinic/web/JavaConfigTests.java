package org.springframework.samples.petclinic.web;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.web.JavaConfigTests.WebConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Tests with Java configuration.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @author Sebastien Deleuze
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy(
{
    @ContextConfiguration(classes = WebConfig.class)
})
public class JavaConfigTests
{

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private OwnerResource personController;

  private MockMvc mockMvc;

  @Before
  public void setup()
  {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    verifyRootWacSupport();
  }

  @Test
  public void deleteOwnerWrongMethod() throws Exception
  {
    this.mockMvc.perform(get("/owner/delete/5").accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isMethodNotAllowed());
  }


  @Test
  public void deleteOwnerOkMethod() throws Exception
  {
    this.mockMvc.perform(MockMvcRequestBuilders.delete("/owner/delete/5").accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void deleteOwnerNotFound() throws Exception
  {
    this.mockMvc.perform(MockMvcRequestBuilders.delete("/owner/delete/6").accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("Oops"));
  }

  @Test
  public void findOwner() throws Exception
  {
    String jsonRep = "{\"id\":null,\"firstName\":\"First-Name\",\"lastName\":\"Last-Name\",\"address\":\"Address\",\"city\":\"City\",\"telephone\":\"te\",\"pets\":[],\"new\":true}";
    this.mockMvc.perform(MockMvcRequestBuilders.get("/owner/5").accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isOk()).andExpect(content().string(jsonRep));

  }

  /**
   * Verify that the breaking change introduced in
   * <a href="https://jira.spring.io/browse/SPR-12553">SPR-12553</a> has been
   * reverted.
   *
   * <p>
   * This code has been copied from
   * {@link org.springframework.test.context.hierarchies.web.ControllerIntegrationTests}
   * .
   *
   * @see org.springframework.test.context.hierarchies.web.ControllerIntegrationTests#verifyRootWacSupport()
   */
  private void verifyRootWacSupport()
  {
    assertNotNull(personController);
  }

  @Configuration
  @EnableWebMvc
  @ComponentScan(basePackages =
  {
      "org.springframework.samples.petclinic.web", "org.springframework.samples.petclinic.service"
  })
  static class WebConfig extends WebMvcConfigurerAdapter
  {
    // mock repositories (db layer)

    @Bean
    OwnerRepository ownerRepo()
    {
      OwnerRepository owner = Mockito.mock(OwnerRepository.class);
      Mockito.when(owner.delete(6)).thenThrow(new IllegalStateException());
      Owner mock = new Owner();
      mock.setAddress("Address");
      mock.setCity("City");
      mock.setFirstName("First-Name");
      mock.setLastName("Last-Name");
      mock.setTelephone("te");
      Mockito.when(owner.findById(5)).thenReturn(mock);
      return owner;
    }

    @Bean
    PetRepository petRepo()
    {
      return Mockito.mock(PetRepository.class);
    }

    @Bean
    VetRepository vetRepo()
    {
      return Mockito.mock(VetRepository.class);
    }

    @Bean
    VisitRepository visitRepo()
    {
      return Mockito.mock(VisitRepository.class);
    }

  }

}
