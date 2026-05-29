import { useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  FaQrcode,
  FaClipboardList,
  FaMobileAlt,
  FaPalette,
  FaChartLine,
  FaBolt,
  FaTable,
  FaComments,
  FaCheckCircle,
  FaStar,
  FaInstagram,
  FaTwitter,
  FaLinkedin,
  FaFacebookF,
} from 'react-icons/fa';
import { FiArrowRight, FiExternalLink, FiMenu, FiX } from 'react-icons/fi';
import './LandingPage.css';

const featureItems = [
  {
    icon: FaQrcode,
    title: 'QR Code Menu',
    description: 'Enable instant menu access with a branded QR experience for every table.',
  },
  {
    icon: FaClipboardList,
    title: 'Contactless Ordering',
    description: 'Guests order from their phone and send requests directly to the kitchen.',
  },
  {
    icon: FaPalette,
    title: 'Real-Time Menu Updates',
    description: 'Publish specials, availability, and pricing instantly across all devices.',
  },
  {
    icon: FaMobileAlt,
    title: 'Mobile Friendly',
    description: 'A seamless, responsive menu that looks polished on every screen size.',
  },
  {
    icon: FaChartLine,
    title: 'Restaurant Dashboard',
    description: 'Manage orders, menus, and staff performance from a single command center.',
  },
  {
    icon: FaBolt,
    title: 'Analytics & Insights',
    description: 'Track orders, peak hours, and menu conversions with actionable dashboards.',
  },
  {
    icon: FaBolt,
    title: 'Fast Ordering Experience',
    description: 'Reduce friction with a polished ordering flow built for hospitality speed.',
  },
  {
    icon: FaTable,
    title: 'Easy Table Management',
    description: 'Assign tables, monitor occupancy, and keep staff in sync with orders.',
  },
];

const steps = [
  {
    step: '01',
    title: 'Scan QR Code',
    description: 'Guests scan a sleek table QR and open your digital menu instantly.',
  },
  {
    step: '02',
    title: 'Browse Digital Menu',
    description: 'Showcase dishes, dietary tags, and featured items with beautiful visuals.',
  },
  {
    step: '03',
    title: 'Order Instantly',
    description: 'Send contactless orders directly to the kitchen and speed up service.',
  },
];

const benefits = [
  'Faster service without paper menus',
  'Reduced waiting time for guests',
  'Secure contactless dining experience',
  'Better satisfaction through modern branding',
  'Easy menu updates in seconds',
  'Built for restaurant technology teams',
];

const testimonialItems = [
  {
    name: 'Maria Chen',
    title: 'Restaurant Owner',
    quote: 'DineOS transformed our dining floor. Orders move faster, guests love the polished QR experience, and our team stays aligned.',
    rating: 5,
  },
  {
    name: 'Noah Patel',
    title: 'Operations Manager',
    quote: 'The dashboard makes it easy to manage menus, track demand, and support staff in real time. The brand feel is premium.',
    rating: 5,
  },
  {
    name: 'Elena Rossi',
    title: 'Head Chef',
    quote: 'Contactless ordering reduced errors and gave our kitchen a smooth workflow. The analytics are a game changer.',
    rating: 5,
  },
];

const pricingPlans = [
  {
    name: 'Starter',
    price: '$29',
    description: 'For new restaurants building their digital menu presence.',
    features: ['QR menu setup', 'Order tracking', 'Basic analytics', 'Email support'],
    highlight: false,
  },
  {
    name: 'Professional',
    price: '$79',
    description: 'Best for growing restaurants that need advanced control and insights.',
    features: ['Unlimited menus', 'Real-time updates', 'Advanced analytics', 'Priority support'],
    highlight: true,
  },
  {
    name: 'Enterprise',
    price: 'Custom',
    description: 'Tailored for multi-location restaurants and brand teams.',
    features: ['Custom onboarding', 'Dedicated account manager', 'API access', 'SLA-backed uptime'],
    highlight: false,
  },
];

const faqItems = [
  {
    question: 'Can I update menu items in real time?',
    answer: 'Yes. DineOS lets you publish menu changes instantly across all guest devices, so specials and sold-out items stay accurate.',
  },
  {
    question: 'Does DineOS support multiple restaurant locations?',
    answer: 'Absolutely. Manage several venues from one dashboard and maintain consistent branding across all menus.',
  },
  {
    question: 'Can customers order without creating an account?',
    answer: 'Yes. Guests scan the QR code and order directly from their browser without requiring a login.',
  },
  {
    question: 'Is the system optimized for mobile devices?',
    answer: 'The entire experience is built mobile-first with polished tablet and phone layouts for guests and staff.',
  },
];

export default function LandingPage() {
  const [activeFaq, setActiveFaq] = useState(0);
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const navItems = ['Home', 'Features', 'About', 'Pricing', 'Contact'];

  return (
    <div className="landing-shell">
      <motion.header
        className={`landing-nav ${mobileNavOpen ? 'menu-open' : ''}`}
        initial={{ y: -30, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.6, ease: 'easeOut' }}
      >
        <Link to="/" className="nav-brand">
          <span className="brand-mark">D</span>
          <span>DineOS</span>
        </Link>
        <nav className="nav-links nav-links-desktop">
          {navItems.map((item) => (
            <a key={item} href={`#${item.toLowerCase()}`} onClick={() => setMobileNavOpen(false)}>
              {item}
            </a>
          ))}
        </nav>
        <div className="nav-actions nav-actions-desktop">
          <Link className="nav-login" to="/login">
            Login
          </Link>
          <Link className="btn btn-primary" to="/register">
            Get Started
          </Link>
        </div>
        <button
          type="button"
          className="nav-toggle"
          aria-expanded={mobileNavOpen}
          aria-label={mobileNavOpen ? 'Close navigation menu' : 'Open navigation menu'}
          onClick={() => setMobileNavOpen((open) => !open)}
        >
          {mobileNavOpen ? <FiX /> : <FiMenu />}
        </button>
        <div className="nav-mobile-panel">
          <nav className="nav-links nav-links-mobile">
            {navItems.map((item) => (
              <a key={item} href={`#${item.toLowerCase()}`} onClick={() => setMobileNavOpen(false)}>
                {item}
              </a>
            ))}
          </nav>
          <div className="nav-actions nav-actions-mobile">
            <Link className="nav-login" to="/login" onClick={() => setMobileNavOpen(false)}>
              Login
            </Link>
            <Link className="btn btn-primary" to="/register" onClick={() => setMobileNavOpen(false)}>
              Get Started
            </Link>
          </div>
        </div>
      </motion.header>

      <main className="landing-content">
        <section className="hero-section" id="home">
          <div className="hero-copy">
            <motion.div
              className="eyebrow"
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.1 }}
            >
              Premium Restaurant Technology
            </motion.div>
            <motion.h1
              className="hero-title"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.2 }}
            >
              Smart QR Menu System for Modern Restaurants
            </motion.h1>
            <motion.p
              className="hero-text"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.3 }}
            >
              Transform your restaurant experience with digital QR menus, contactless ordering, real-time menu updates, and seamless customer interaction.
            </motion.p>
            <motion.div
              className="hero-actions"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.35 }}
            >
              <Link className="btn btn-primary" to="/register">
                Get Started
                <FiArrowRight />
              </Link>
              <a className="btn btn-secondary" href="#dashboard">
                Live Demo
                <FiExternalLink />
              </a>
            </motion.div>
            <motion.div
              className="hero-stat"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7, delay: 0.45 }}
            >
              <div className="stat-card">
                <span>42%</span>
                <p>Faster table turnover with contactless ordering.</p>
              </div>
              <div className="stat-card">
                <span>98%</span>
                <p>Restaurant owners see better guest satisfaction.</p>
              </div>
            </motion.div>
          </div>

          <motion.div
            className="hero-visual"
            initial={{ opacity: 0, scale: 0.95, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.25, ease: 'easeOut' }}
          >
            <div className="visual-glow visual-glow-large" />
            <div className="visual-glow visual-glow-small" />
            <div className="mockup-card">
              <div className="mockup-header">
                <div className="mockup-dot orange" />
                <div className="mockup-dot" />
                <div className="mockup-dot" />
              </div>
              <div className="mockup-body">
                <div className="mockup-badge">QR Menu Live</div>
                <div className="mockup-item">
                  <div>
                    <h3>Bruschetta</h3>
                    <span>Fresh tomato, basil & olive oil</span>
                  </div>
                  <span>$14</span>
                </div>
                <div className="mockup-item soft">
                  <div>
                    <h3>Spicy Shrimp</h3>
                    <span>Grilled with citrus glaze</span>
                  </div>
                  <span>$18</span>
                </div>
                <div className="mockup-item">
                  <div>
                    <h3>Chef’s Salad</h3>
                    <span>Seasonal greens and house dressing</span>
                  </div>
                  <span>$11</span>
                </div>
              </div>
            </div>
            <div className="mockup-panel">
              <div className="panel-top">
                <div className="panel-dot" />
                <div className="panel-dot" />
                <div className="panel-dot" />
              </div>
              <div className="panel-hero">
                <div className="panel-status">
                  <span>Table 12</span>
                  <strong>Order #324</strong>
                </div>
                <div className="panel-chips">
                  <span>Pending</span>
                  <span>Kitchen</span>
                </div>
                <div className="panel-line" />
                <div className="panel-row">
                  <div>
                    <p>Margherita Pizza</p>
                    <small>Qty 2</small>
                  </div>
                  <strong>$26</strong>
                </div>
                <div className="panel-row">
                  <div>
                    <p>Dirty Fries</p>
                    <small>Qty 1</small>
                  </div>
                  <strong>$12</strong>
                </div>
              </div>
            </div>
          </motion.div>
        </section>

        <section className="section features-section" id="features">
          <div className="section-head">
            <span className="section-label">Features</span>
            <h2>Built for restaurants that want modern guest flow.</h2>
          </div>
          <div className="feature-grid">
            {featureItems.map(({ icon: Icon, title, description }, index) => (
              <motion.article
                className="feature-card"
                key={title}
                whileHover={{ y: -8, boxShadow: '0 24px 60px rgba(255,122,0,0.15)' }}
                initial={{ opacity: 0, y: 25 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.25 }}
                transition={{ duration: 0.5, delay: index * 0.05, ease: 'easeOut' }}
              >
                <div className="feature-icon">
                  <Icon />
                </div>
                <h3>{title}</h3>
                <p>{description}</p>
              </motion.article>
            ))}
          </div>
        </section>

        <section className="section steps-section" id="about">
          <div className="section-head">
            <span className="section-label">How it works</span>
            <h2>Three simple steps from scan to order.</h2>
          </div>
          <div className="timeline">
            {steps.map(({ step, title, description }, index) => (
              <motion.div
                className="timeline-step"
                key={step}
                initial={{ opacity: 0, x: index % 2 === 0 ? -30 : 30 }}
                whileInView={{ opacity: 1, x: 0 }}
                viewport={{ once: true, amount: 0.25 }}
                transition={{ duration: 0.5, delay: 0.1 + index * 0.1 }}
              >
                <div className="timeline-number">{step}</div>
                <div className="timeline-content">
                  <h3>{title}</h3>
                  <p>{description}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </section>

        <section className="section dashboard-section" id="dashboard">
          <div className="section-head">
            <span className="section-label">Dashboard Preview</span>
            <h2>Command center for modern restaurants.</h2>
          </div>
          <div className="dashboard-grid">
            <motion.div
              className="dashboard-panel"
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, amount: 0.3 }}
              transition={{ duration: 0.6 }}
            >
              <div className="dashboard-header">
                <h3>Live order board</h3>
                <span>Updated instantly</span>
              </div>
              <div className="dashboard-list">
                <div className="dashboard-item">
                  <div>
                    <h4>Table 6</h4>
                    <small>2 items • Preparing</small>
                  </div>
                  <span>3:12</span>
                </div>
                <div className="dashboard-item active">
                  <div>
                    <h4>Table 11</h4>
                    <small>5 items • Ready</small>
                  </div>
                  <span>1:25</span>
                </div>
                <div className="dashboard-item">
                  <div>
                    <h4>Table 3</h4>
                    <small>Drink order • Sent</small>
                  </div>
                  <span>0:42</span>
                </div>
              </div>
            </motion.div>
            <motion.div
              className="dashboard-panel dashboard-graph"
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, amount: 0.3 }}
              transition={{ duration: 0.6, delay: 0.1 }}
            >
              <div className="dashboard-header">
                <h3>Restaurant analytics</h3>
                <span>Performance at a glance</span>
              </div>
              <div className="stats-grid">
                <div>
                  <h4>80%</h4>
                  <p>Order completion</p>
                </div>
                <div>
                  <h4>4.9</h4>
                  <p>Guest rating</p>
                </div>
                <div>
                  <h4>12m</h4>
                  <p>Average service time</p>
                </div>
              </div>
            </motion.div>
          </div>
        </section>

        <section className="section benefits-section" id="pricing">
          <div className="benefits-copy">
            <div className="section-label">Benefits</div>
            <h2>Restaurant teams win with DineOS.</h2>
            <p>
              Deliver faster service, reduce manual work, and create a premium digital dining experience for every guest.
            </p>
          </div>
          <div className="benefits-list">
            {benefits.map((benefit) => (
              <motion.div
                key={benefit}
                className="benefit-card"
                whileHover={{ y: -6 }}
                transition={{ duration: 0.25 }}
              >
                <div className="benefit-icon">
                  <FaCheckCircle />
                </div>
                <p>{benefit}</p>
              </motion.div>
            ))}
          </div>
        </section>

        <section className="section testimonials-section" id="contact">
          <div className="section-head">
            <span className="section-label">Testimonials</span>
            <h2>Trusted by modern restaurant teams.</h2>
          </div>
          <div className="testimonial-grid">
            {testimonialItems.map(({ name, title, quote, rating }) => (
              <motion.article
                className="testimonial-card"
                key={name}
                initial={{ opacity: 0, y: 25 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.3 }}
                transition={{ duration: 0.55 }}
              >
                <div className="testimonial-avatar">
                  <span>{name.split(' ').map((part) => part[0]).join('')}</span>
                </div>
                <div className="testimonial-body">
                  <p>{quote}</p>
                  <div className="testimonial-meta">
                    <div>
                      <strong>{name}</strong>
                      <span>{title}</span>
                    </div>
                    <div className="testimonial-rating">
                      {Array.from({ length: rating }).map((_, index) => (
                        <FaStar key={index} />
                      ))}
                    </div>
                  </div>
                </div>
              </motion.article>
            ))}
          </div>
        </section>

        <section className="section pricing-section">
          <div className="section-head">
            <span className="section-label">Pricing</span>
            <h2>Choose the plan that fits your restaurant.</h2>
          </div>
          <div className="pricing-grid">
            {pricingPlans.map(({ name, price, description, features, highlight }) => (
              <motion.article
                className={`pricing-card ${highlight ? 'highlight' : ''}`}
                key={name}
                initial={{ opacity: 0, y: 25 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.3 }}
                transition={{ duration: 0.5 }}
              >
                <div className="pricing-header">
                  <span>{name}</span>
                  <h3>{price}</h3>
                </div>
                <p>{description}</p>
                <ul>
                  {features.map((feature) => (
                    <li key={feature}>{feature}</li>
                  ))}
                </ul>
                <a className="btn btn-primary" href="#contact">
                  Select Plan
                </a>
              </motion.article>
            ))}
          </div>
        </section>

        <section className="section faq-section">
          <div className="section-head">
            <span className="section-label">FAQ</span>
            <h2>Questions restaurants ask most often.</h2>
          </div>
          <div className="faq-list">
            {faqItems.map(({ question, answer }, index) => (
              <motion.div
                className={`faq-card ${activeFaq === index ? 'open' : ''}`}
                key={question}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.2 }}
                transition={{ duration: 0.4, delay: index * 0.05 }}
              >
                <button
                  type="button"
                  className="faq-question"
                  onClick={() => setActiveFaq(activeFaq === index ? -1 : index)}
                  aria-expanded={activeFaq === index}
                >
                  <span>{question}</span>
                  <span>{activeFaq === index ? '-' : '+'}</span>
                </button>
                <div className="faq-answer">
                  <p>{answer}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </section>

        <section className="section cta-section">
          <div className="cta-shell">
            <div>
              <span>Upgrade Your Restaurant with DineOS</span>
              <h2>Launch modern ordering and hospitality today.</h2>
            </div>
            <Link className="btn btn-cta" to="/register">
              Start Free Trial
            </Link>
          </div>
        </section>
      </main>

      <footer className="landing-footer">
        <div className="footer-grid">
          <div>
            <div className="nav-brand footer-brand">
              <span className="brand-mark">D</span>
              <span>DineOS</span>
            </div>
            <p>Restaurant technology designed for QR menus, ordering, and modern guest experiences.</p>
          </div>
          <div>
            <h4>Quick Links</h4>
            <a href="#features">Features</a>
            <a href="#about">How it Works</a>
            <a href="#pricing">Pricing</a>
            <a href="#contact">Contact</a>
          </div>
          <div>
            <h4>Contact</h4>
            <a href="mailto:support@dineos.app">support@dineos.app</a>
            <a href="tel:+12025550123">+1 202 555 0123</a>
          </div>
          <div>
            <h4>Social</h4>
            <div className="social-links">
              <a href="#"><FaInstagram /></a>
              <a href="#"><FaTwitter /></a>
              <a href="#"><FaLinkedin /></a>
              <a href="#"><FaFacebookF /></a>
            </div>
          </div>
        </div>
        <div className="footer-copy">© 2026 DineOS. All rights reserved.</div>
      </footer>
    </div>
  );
}
