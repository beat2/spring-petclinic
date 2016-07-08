package org.springframework.samples.petclinic.web;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.service.ClinicService;
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
        .andDo(print()).andExpect(status().isNotFound());
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
  static class WebConfig extends WebMvcConfigurerAdapter
  {

    @Bean
    public OwnerResource personController()
    {
      ClinicService clinicService = Mockito.mock(ClinicService.class);
      Mockito.doThrow(new IllegalStateException()).when(clinicService).deleteOwner(6);
      return new OwnerResource(clinicService);
    }

    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry)
    // {
    // registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    // }
    //
    // @Override
    // public void addViewControllers(ViewControllerRegistry registry)
    // {
    // registry.addViewController("/").setViewName("home");
    // }
    //
    // @Override
    // public void
    // configureDefaultServletHandling(DefaultServletHandlerConfigurer
    // configurer)
    // {
    // configurer.enable();
    // }

  }

}
