(function () {
    const canvas = document.getElementById("logoCanvas");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");

    const dpr = window.devicePixelRatio || 1;
    let W, H, GROUND;

    function resize() {
        W = canvas.offsetWidth;
        H = canvas.offsetHeight;
        canvas.width  = W * dpr;
        canvas.height = H * dpr;
        ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
        GROUND = H - 120;
    }
    resize();
    window.addEventListener("resize", resize);

    const GRAVITY     = 0.38;
    const RESTITUTION = 0.38;
    const FRICTION    = 0.88;

    let dustParts    = [];
    let sparkParts   = [];
    let debrisParts  = [];
    let bugTriggered = false;
    let shakeAmt     = 0;
    let shakeDecay   = 0;

    const SIGN = {
        get x()   { return W / 2 - 255; },
        get cx()  { return W / 2; },
        get y()   { return 80; },
        w: 510, h: 132, r: 18,
        get bottomY() { return this.y + this.h; },
        get cable1()  { return W / 2 - 160; },
        get cable2()  { return W / 2 + 160; },
        ceilY: 22,
    };

//    Robot bug
    class Bug {
        constructor() {
            this.x     = -100;
            this.baseY = H * 0.26;
            this.y     = this.baseY;
            this.vx    = 5.8;
            this.vy    = 0;
            this.angle = 0;
            this.spinV = 0;
            this.phase = "waiting";
            this.alpha = 1;
            this.tick  = 0;
            this.scale = 1.28;

            setTimeout(() => {
                    this.phase = "flying";  // <-- arranca a volar después del delay
                }, 3000);
        }

        get targetX() { return SIGN.x - 2; }

        drawWings(t) {
            const step   = Math.floor(t * 0.40) % 4;
            const flapUp = [0.60, 0.15, 0.38, 0.50][step];
            for (const s of [-1, 1]) {
                ctx.save();
                ctx.scale(s, 1);
                ctx.save();
                ctx.rotate(-flapUp * s);
                const wg = ctx.createLinearGradient(2, -22, 34, 0);
                wg.addColorStop(0,    "rgba(80,6,6,0.96)");
                wg.addColorStop(0.4,  "rgba(130,14,14,0.88)");
                wg.addColorStop(0.75, "rgba(100,10,10,0.72)");
                wg.addColorStop(1,    "rgba(60,4,4,0.48)");
                ctx.fillStyle   = wg;
                ctx.strokeStyle = "rgba(255,90,90,0.35)";
                ctx.lineWidth   = 0.9;
                ctx.beginPath();
                ctx.moveTo(4, -2);  ctx.lineTo(22, -26);
                ctx.lineTo(35, -16); ctx.lineTo(22, -2);
                ctx.closePath();
                ctx.fill(); ctx.stroke();
                ctx.strokeStyle = "rgba(255,90,90,0.22)";
                ctx.lineWidth   = 0.6;
                ctx.beginPath();
                ctx.moveTo(8,  -6); ctx.lineTo(18, -20);
                ctx.moveTo(18,-20); ctx.lineTo(24, -20);
                ctx.moveTo(14,-14); ctx.lineTo(28, -12);
                ctx.stroke();
                const pulse = 0.35 + step * 0.18;
                ctx.fillStyle   = `rgba(255,110,110,${pulse})`;
                ctx.shadowColor = "rgba(255,50,50,0.70)";
                ctx.shadowBlur  = 8;
                ctx.beginPath(); ctx.arc(22, -20, 2.2, 0, Math.PI * 2); ctx.fill();
                ctx.shadowBlur  = 0;
                ctx.restore();
                ctx.restore();
            }
        }

        drawBody(t) {
            for (let i = 0; i < 4; i++) {
                const bw = 11 - i * 1.8;
                const seg = ctx.createLinearGradient(-bw/2, 3+i*5, bw/2, 3+i*5+5);
                seg.addColorStop(0, i % 2 === 0 ? "#5a0c0c" : "#420808");
                seg.addColorStop(1, i % 2 === 0 ? "#3d0808" : "#2e0505");
                ctx.fillStyle   = seg;
                ctx.strokeStyle = "rgba(255,80,80,0.22)";
                ctx.lineWidth   = 0.7;
                ctx.beginPath();
                if (ctx.roundRect) ctx.roundRect(-bw/2, 3 + i*5, bw, 5, 2);
                else ctx.rect(-bw/2, 3 + i*5, bw, 5);
                ctx.fill(); ctx.stroke();
            }
            const thorax = ctx.createLinearGradient(-8, -10, 8, 6);
            thorax.addColorStop(0,   "#6e1010");
            thorax.addColorStop(0.5, "#4a0a0a");
            thorax.addColorStop(1,   "#350707");
            ctx.fillStyle   = thorax;
            ctx.strokeStyle = "rgba(255,80,80,0.48)";
            ctx.lineWidth   = 1.1;
            ctx.beginPath();
            if (ctx.roundRect) ctx.roundRect(-8, -10, 16, 16, 4);
            else ctx.rect(-8, -10, 16, 16);
            ctx.fill(); ctx.stroke();
            ctx.strokeStyle = "rgba(255,80,80,0.18)";
            ctx.lineWidth   = 0.55;
            ctx.beginPath();
            ctx.moveTo(0, -10); ctx.lineTo(0,  6);
            ctx.moveTo(-8, -2); ctx.lineTo(8, -2);
            ctx.moveTo(-8,  2); ctx.lineTo(8,  2);
            ctx.stroke();
            const head = ctx.createRadialGradient(-2, -16, 1, 0, -14, 9);
            head.addColorStop(0, "#7a1010");
            head.addColorStop(1, "#3a0606");
            ctx.fillStyle   = head;
            ctx.strokeStyle = "rgba(255,80,80,0.55)";
            ctx.lineWidth   = 1.1;
            ctx.beginPath(); ctx.arc(0, -15, 8.5, 0, Math.PI*2); ctx.fill(); ctx.stroke();
            ctx.fillStyle = "rgba(255,140,140,0.18)";
            ctx.beginPath(); ctx.ellipse(-2, -19, 4, 2.5, -0.4, 0, Math.PI*2); ctx.fill();
            const ep = 0.65 + Math.sin(t * 0.07) * 0.35;
            ctx.fillStyle   = `rgba(255,110,110,${ep})`;
            ctx.shadowColor = "rgba(255,50,50,0.90)";
            ctx.shadowBlur  = 10;
            ctx.beginPath();
            ctx.arc(-3.2, -15.5, 2.4, 0, Math.PI*2);
            ctx.arc( 3.2, -15.5, 2.4, 0, Math.PI*2);
            ctx.fill();
            ctx.shadowBlur = 0;
            ctx.fillStyle  = "rgba(255,200,200,0.55)";
            ctx.beginPath();
            ctx.arc(-3.8, -16.4, 0.8, 0, Math.PI*2);
            ctx.arc( 2.6, -16.4, 0.8, 0, Math.PI*2);
            ctx.fill();
        }

        drawLegs(t) {
            const legSwing = Math.sin(t * 0.38) * 0.28;
            ctx.strokeStyle = "rgba(180,60,60,0.65)";
            ctx.lineWidth   = 1.0;
            const legPos = [[-8, 0], [-8, 6], [-8, 12]];
            for (const s of [-1, 1]) {
                legPos.forEach(([lx, ly], i) => {
                    const phase = legSwing * (i % 2 === 0 ? 1 : -1) * s;
                    ctx.beginPath();
                    ctx.moveTo(lx * s, ly);
                    ctx.lineTo((lx - 10) * s, ly + 4 + phase * 8);
                    ctx.lineTo((lx - 18) * s, ly + 8 + phase * 14);
                    ctx.stroke();
                    ctx.fillStyle = "rgba(150,40,40,0.75)";
                    ctx.beginPath(); ctx.arc((lx - 18) * s, ly + 8 + phase * 14, 1.2, 0, Math.PI*2); ctx.fill();
                });
            }
        }

        drawAntennae(t) {
            const ap = 0.55 + Math.sin(t * 0.065) * 0.45;
            ctx.strokeStyle = "rgba(255,80,80,0.65)";
            ctx.lineWidth   = 1.1;
            for (const s of [-1, 1]) {
                const wobble = Math.sin(t * 0.05 + s) * 3;
                ctx.beginPath();
                ctx.moveTo(s * 4, -22);
                ctx.lineTo(s * 10, -33 + wobble);
                ctx.lineTo(s * 16, -30 + wobble);
                ctx.stroke();
                ctx.fillStyle   = `rgba(255,120,120,${ap})`;
                ctx.shadowColor = "rgba(255,60,60,0.70)";
                ctx.shadowBlur  = 7;
                ctx.beginPath(); ctx.arc(s*16, -30+wobble, 2.2, 0, Math.PI*2); ctx.fill();
            }
            ctx.shadowBlur = 0;
        }

        draw() {
            if (this.alpha <= 0) return;
            ctx.save();
            ctx.globalAlpha = this.alpha;
            ctx.translate(this.x, this.y);
            ctx.rotate(this.angle);
            ctx.scale(this.scale, this.scale);
            this.drawLegs(this.tick);
            this.drawWings(this.tick);
            this.drawBody(this.tick);
            this.drawAntennae(this.tick);
            ctx.restore();
        }

        update() {
            if (this.phase === "waiting") return;
            this.tick++;
            if (this.phase === "flying") {
                this.x += this.vx;
                this.y  = this.baseY + Math.sin(this.x * 0.038) * 22;
                this.angle = Math.cos(this.x * 0.038) * 0.065;
                if (this.x >= this.targetX) {
                    this.phase   = "dead";
                    bugTriggered = true;
                    spawnImpact(this.x, this.y);
                    shakeAmt   = 5.5;
                    shakeDecay = 0.78;
                    this.vx   = -2.8;
                    this.vy   = -5.0;
                    this.spinV = 0.26;
                }
            } else if (this.phase === "dead") {
                this.vy    += GRAVITY * 0.72;
                this.x     += this.vx;
                this.y     += this.vy;
                this.angle += this.spinV;
                this.spinV *= 0.96;
                this.vx    *= 0.97;
                if (this.y > H + 80) this.alpha = 0;
            }
        }
    }

//  Letrero
    class Letter {
        constructor(char, cx, fallDelay, isP = false) {
            this.char      = char;
            this.x         = cx;
            this.y         = SIGN.y + SIGN.h / 2 + 5;
            this.fallDelay = fallDelay;
            this.isP       = isP;
            this.vx = 0; this.vy = 0;
            this.rot = 0; this.rotV = 0;
            this.bounces   = 0;
            this.state     = "fixed";

            this.flickerTick  = 0;
            this.flickerAlpha = 1;

            this.pivotX = cx;
            this.pivotY = SIGN.bottomY;
            this.armLen = 65;
            this.pAngle = 0;
            this.pVel   = 0;
            this.pDamp  = 0.988;
            this.pGrav  = 0.0038;

            this.groundShadowAlpha = 0;
        }

        drawGlyph(glowOn, glowAlpha) {
            ctx.font = "800 100px 'DM Sans', sans-serif";
            ctx.textAlign    = "center";
            ctx.textBaseline = "middle";

            for (let i = 7; i >= 1; i--) {
                const fade = 0.78 - i * 0.09;
                ctx.fillStyle = `rgba(12,28,52,${fade})`;
                ctx.fillText(this.char, i * 0.9, i * 0.9);
            }

            const faceGrad = ctx.createLinearGradient(0, -48, 0, 48);
            faceGrad.addColorStop(0,    "#dce8f8");
            faceGrad.addColorStop(0.45, "#c8d8ee");
            faceGrad.addColorStop(1,    "#b8cce0");
            ctx.fillStyle = faceGrad;
            ctx.fillText(this.char, 0, 0);

            ctx.fillStyle = "rgba(255,255,255,0.42)";
            ctx.fillText(this.char, -1.5, -2.2);

            ctx.fillStyle = "rgba(255,255,255,0.16)";
            ctx.fillText(this.char, -0.5, -0.8);

            if (glowOn && glowAlpha > 0) {
                ctx.save();
                ctx.globalAlpha = glowAlpha * 0.85;
                ctx.shadowColor = "rgba(8,50,105,0.98)";
                ctx.shadowBlur  = 36;
                ctx.fillStyle   = "#0A3470";
                ctx.fillText(this.char, 0, 0);
                ctx.restore();

                ctx.save();
                ctx.globalAlpha = glowAlpha * 0.45;
                ctx.shadowColor = "rgba(20,80,180,0.60)";
                ctx.shadowBlur  = 18;
                ctx.fillStyle   = "rgba(12,58,128,0.65)";
                ctx.fillText(this.char, 0, 0);
                ctx.shadowBlur  = 0;
                ctx.restore();
            }
        }

        drawCable(wx, wy) {
            ctx.save();
            const midX = this.pivotX + (wx - this.pivotX) * 0.12;
            const midY = (this.pivotY + wy) / 2 + 10;
            ctx.strokeStyle = "rgba(0,0,0,0.12)";
            ctx.lineWidth   = 2.8;
            ctx.lineCap     = "round";
            ctx.beginPath();
            ctx.moveTo(this.pivotX + 1, this.pivotY + 2);
            ctx.quadraticCurveTo(midX + 1, midY + 2, wx + 1, wy + 2);
            ctx.stroke();
            const cg = ctx.createLinearGradient(this.pivotX, this.pivotY, wx, wy);
            cg.addColorStop(0,   "rgba(130,145,162,0.90)");
            cg.addColorStop(0.5, "rgba(160,175,192,0.75)");
            cg.addColorStop(1,   "rgba(110,128,145,0.82)");
            ctx.strokeStyle = cg;
            ctx.lineWidth   = 2.2;
            ctx.beginPath();
            ctx.moveTo(this.pivotX, this.pivotY);
            ctx.quadraticCurveTo(midX, midY, wx, wy);
            ctx.stroke();
            ctx.strokeStyle = "rgba(220,230,242,0.35)";
            ctx.lineWidth   = 0.8;
            ctx.beginPath();
            ctx.moveTo(this.pivotX, this.pivotY);
            ctx.quadraticCurveTo(midX - 1, midY, wx - 1, wy);
            ctx.stroke();
            ctx.restore();
        }

        drawGroundShadow() {
            if (this.groundShadowAlpha <= 0) return;
            const dist = Math.max(1, GROUND - this.y);
            const scaleX = 1 + dist * 0.004;
            const scaleY = 0.12 + Math.min(dist * 0.001, 0.28);
            ctx.save();
            ctx.translate(this.x, GROUND);
            ctx.scale(scaleX, scaleY);
            ctx.globalAlpha = this.groundShadowAlpha * Math.max(0, 1 - dist / 320);
            const sg = ctx.createRadialGradient(0, 0, 2, 0, 0, 30);
            sg.addColorStop(0,   "rgba(0,15,35,0.55)");
            sg.addColorStop(0.5, "rgba(0,15,35,0.22)");
            sg.addColorStop(1,   "rgba(0,15,35,0)");
            ctx.fillStyle = sg;
            ctx.beginPath(); ctx.ellipse(0, 0, 30, 12, 0, 0, Math.PI*2); ctx.fill();
            ctx.restore();
        }

        draw() {
            let glowOn    = false;
            let glowAlpha = 0;

            if (this.state === "fixed") {
                glowOn    = true;
                glowAlpha = 1;
                this.groundShadowAlpha = 0;
            } else if (this.state === "hanging") {
                glowOn    = true;
                glowAlpha = this.flickerAlpha;
            } else {
                this.groundShadowAlpha = 0.9;
                this.drawGroundShadow();
            }

            if (this.state === "hanging") {
                const wx = this.pivotX + Math.sin(this.pAngle) * this.armLen;
                const wy = this.pivotY + Math.cos(this.pAngle) * this.armLen;
                this.drawCable(wx, wy);
                ctx.save();
                ctx.translate(wx, wy);
                ctx.rotate(this.pAngle);
                ctx.shadowColor   = "rgba(0,0,0,0.20)";
                ctx.shadowBlur    = 22;
                ctx.shadowOffsetY = 10;
                this.drawGlyph(glowOn, glowAlpha);
                ctx.restore();
            } else {
                const above = Math.max(0, GROUND - this.y);
                ctx.save();
                ctx.translate(this.x, this.y);
                ctx.rotate(this.rot);
                ctx.shadowColor   = `rgba(0,10,30,${Math.min(0.24, 0.04 + above * 0.00035)})`;
                ctx.shadowBlur    = 6 + above * 0.045;
                ctx.shadowOffsetY = 8 + above * 0.025;
                this.drawGlyph(glowOn, glowAlpha);
                ctx.restore();
            }
        }

        update() {
            if (bugTriggered && this.state === "fixed") {
                if (this.isP) {
                    setTimeout(() => {
                        this.state  = "hanging";
                        this.pAngle = -0.48;
                        this.pVel   =  0.042;
                    }, 100);
                } else {
                    setTimeout(() => {
                        if (this.state !== "fixed") return;
                        this.state = "falling";
                        this.vx   = this.char === "I" ? -0.8 : (Math.random() - 0.5) * 3.2;
                        this.rotV = this.char === "I" ? -0.04 : (Math.random() - 0.5) * 0.085;
                        this.vy   = -0.5;
                    }, this.fallDelay);
                }
            }

            if (this.state === "falling" || this.state === "bouncing") {
                this.vy  += GRAVITY;
                this.y   += this.vy;
                this.x   += this.vx;
                this.rot += this.rotV;

                if (this.y >= GROUND) {
                    this.y = GROUND;
                    if (Math.abs(this.vy) > 1.3) {
                        this.vy   *= -RESTITUTION;
                        this.vx   *= FRICTION;
                        this.rotV *= 0.42;
                        this.bounces++;
                        this.state = "bouncing";
                        if (this.bounces <= 3) spawnBounce(this.x, GROUND, this.bounces);
                        if (this.bounces === 1) spawnDebris(this.x, GROUND);
                    } else {
                        this.vy   = 0;
                        this.vx  *= 0.70;
                        this.rotV *= 0.46;
                        if (Math.abs(this.rotV) < 0.0014 && Math.abs(this.vx) < 0.09)
                            this.state = "landed";
                    }
                }
            }

            if (this.state === "hanging") {
                this.pVel   -= this.pGrav * Math.sin(this.pAngle);
                this.pVel   *= this.pDamp;
                this.pAngle += this.pVel;
                if (Math.abs(this.pVel) < 0.00038)
                    this.pVel = 0.00038 * (this.pAngle > 0 ? -1 : 1);

                this.flickerTick++;
                const ft = this.flickerTick;
                if (ft % 9 < 2) {
                    this.flickerAlpha = 0.02 + Math.random() * 0.10;
                } else if (ft % 17 < 3) {
                    this.flickerAlpha = 0.55 + Math.random() * 0.45;
                } else if (ft % 37 < 5) {
                    this.flickerAlpha = 0;
                } else if (ft % 23 < 2) {
                    this.flickerAlpha = 1;
                } else {
                    this.flickerAlpha = 0.68 + Math.sin(ft * 0.22) * 0.32;
                }
            }
        }
    }


    function spawnImpact(x, y) {
        for (let i = 0; i < 22; i++) {
            const a = Math.random() * Math.PI * 2, spd = 2.5 + Math.random() * 9;
            sparkParts.push({
                x, y,
                vx: Math.cos(a) * spd,
                vy: Math.sin(a) * spd - 4,
                life: 1,
                size: 0.9 + Math.random() * 2.8,
                hue: Math.random() < 0.6 ? (Math.random() * 30) : (200 + Math.random() * 40)
            });
        }
        for (let i = 0; i < 14; i++) {
            dustParts.push({
                x: x + (Math.random()-0.5)*26,
                y: y + (Math.random()-0.5)*12,
                vx: (Math.random()-0.5)*6.5,
                vy: (Math.random()-0.5)*6 - 2,
                life: 1,
                size: 2.2 + Math.random() * 3.8,
                hue: 210 + Math.random() * 30
            });
        }
    }

    function spawnBounce(x, y, n) {
        const count = Math.max(3, 7 - n * 2);
        for (let i = 0; i < count; i++) {
            dustParts.push({
                x: x + (Math.random()-0.5)*38, y,
                vx: (Math.random()-0.5)*5,
                vy: -(Math.random()*3.5 + 0.8),
                life: 0.75,
                size: 1.8 + Math.random() * 2.5,
                hue: 208 + Math.random() * 25
            });
        }
        if (n === 1) {
            for (let i = 0; i < 6; i++) {
                const a = -Math.PI + Math.random() * Math.PI;
                sparkParts.push({
                    x, y,
                    vx: Math.cos(a) * 3,
                    vy: Math.sin(a) * 3 - 2,
                    life: 0.6,
                    size: 0.7 + Math.random() * 1.5,
                    hue: Math.random() * 40
                });
            }
        }
    }

    function spawnDebris(x, y) {
        for (let i = 0; i < 5; i++) {
            debrisParts.push({
                x: x + (Math.random()-0.5)*20, y,
                vx: (Math.random()-0.5)*4,
                vy: -(Math.random()*4 + 1),
                rot: Math.random() * Math.PI * 2,
                rotV: (Math.random()-0.5) * 0.3,
                life: 1,
                w: 3 + Math.random() * 7,
                h: 2 + Math.random() * 4
            });
        }
    }

    function updateParticles() {
        for (let i = dustParts.length-1; i >= 0; i--) {
            const p = dustParts[i];
            p.x += p.vx; p.y += p.vy;
            p.vy += 0.07; p.vx *= 0.965; p.life -= 0.016;
            if (p.life <= 0) { dustParts.splice(i, 1); continue; }
            const h = p.hue || 215;
            ctx.fillStyle = `hsla(${h},18%,56%,${p.life * 0.50})`;
            ctx.beginPath(); ctx.arc(p.x, p.y, p.size * p.life, 0, Math.PI*2); ctx.fill();
        }
        for (let i = sparkParts.length-1; i >= 0; i--) {
            const s = sparkParts[i];
            const px = s.x, py = s.y;
            s.x += s.vx; s.y += s.vy;
            s.vy += 0.22; s.vx *= 0.955; s.life -= 0.022;
            if (s.life <= 0) { sparkParts.splice(i, 1); continue; }
            ctx.strokeStyle = `hsla(${s.hue},88%,58%,${s.life * 0.88})`;
            ctx.lineWidth = s.size * 0.50 * s.life; ctx.lineCap = "round";
            ctx.beginPath(); ctx.moveTo(px, py); ctx.lineTo(s.x, s.y); ctx.stroke();
            ctx.fillStyle   = `hsla(${s.hue},92%,65%,${s.life})`;
            ctx.shadowColor = `hsla(${s.hue},92%,65%,0.60)`;
            ctx.shadowBlur  = 5;
            ctx.beginPath(); ctx.arc(s.x, s.y, s.size * s.life * 0.8, 0, Math.PI*2); ctx.fill();
            ctx.shadowBlur  = 0;
        }
        for (let i = debrisParts.length-1; i >= 0; i--) {
            const d = debrisParts[i];
            d.x += d.vx; d.y += d.vy;
            d.vy += GRAVITY * 0.6; d.vx *= 0.97;
            d.rot += d.rotV; d.life -= 0.012;
            if (d.life <= 0 || d.y > GROUND + 20) { debrisParts.splice(i, 1); continue; }
            ctx.save();
            ctx.globalAlpha = d.life * 0.7;
            ctx.translate(d.x, d.y); ctx.rotate(d.rot);
            ctx.fillStyle = `rgba(185,200,220,${d.life * 0.65})`;
            ctx.fillRect(-d.w/2, -d.h/2, d.w, d.h);
            ctx.restore();
        }
    }

    function drawSignBoard() {
        const { x, y, w, h, r, ceilY } = SIGN;

        ctx.lineCap = "round";
        for (const bx of [SIGN.cable1, SIGN.cable2]) {
            ctx.strokeStyle = "rgba(0,0,0,0.10)";
            ctx.lineWidth   = 3.2;
            ctx.beginPath();
            ctx.moveTo(bx + 1, ceilY + 5);
            ctx.quadraticCurveTo(bx, (ceilY + y) / 2, bx + 1, y + 2);
            ctx.stroke();
            const cg = ctx.createLinearGradient(bx, ceilY, bx, y);
            cg.addColorStop(0,   "rgba(180,192,205,0.90)");
            cg.addColorStop(0.5, "rgba(200,212,226,0.80)");
            cg.addColorStop(1,   "rgba(160,175,190,0.85)");
            ctx.strokeStyle = cg;
            ctx.lineWidth   = 2.2;
            ctx.beginPath();
            ctx.moveTo(bx, ceilY + 4);
            ctx.quadraticCurveTo(bx - 1, (ceilY + y) / 2, bx, y);
            ctx.stroke();
            ctx.fillStyle   = "#c8d2de";
            ctx.strokeStyle = "rgba(140,155,168,0.70)";
            ctx.lineWidth   = 1.0;
            ctx.beginPath(); ctx.arc(bx, ceilY + 4, 4.5, 0, Math.PI*2); ctx.fill(); ctx.stroke();
            ctx.fillStyle = "rgba(255,255,255,0.42)";
            ctx.beginPath(); ctx.arc(bx - 1, ceilY + 3, 1.8, 0, Math.PI*2); ctx.fill();
        }

        ctx.shadowColor   = "rgba(0,12,35,0.22)";
        ctx.shadowBlur    = 32;
        ctx.shadowOffsetY = 12;
        ctx.shadowOffsetX = 3;

        const bg = ctx.createLinearGradient(x, y, x, y + h);
        bg.addColorStop(0,    "#f0f4fa");
        bg.addColorStop(0.25, "#e8eef6");
        bg.addColorStop(0.6,  "#dce4f0");
        bg.addColorStop(1,    "#ccd6e8");
        ctx.fillStyle = bg;
        ctx.beginPath(); ctx.roundRect(x, y, w, h, r); ctx.fill();

        ctx.shadowColor   = "transparent";
        ctx.shadowBlur    = 0;
        ctx.shadowOffsetY = 0;
        ctx.shadowOffsetX = 0;

        const border = ctx.createLinearGradient(x, y, x, y + h);
        border.addColorStop(0,   "rgba(210,222,238,0.90)");
        border.addColorStop(0.5, "rgba(185,200,218,0.70)");
        border.addColorStop(1,   "rgba(165,182,200,0.80)");
        ctx.strokeStyle = border;
        ctx.lineWidth   = 1.5;
        ctx.beginPath(); ctx.roundRect(x, y, w, h, r); ctx.stroke();

        const gloss = ctx.createLinearGradient(x, y, x, y + 36);
        gloss.addColorStop(0,   "rgba(255,255,255,0.72)");
        gloss.addColorStop(0.7, "rgba(255,255,255,0.18)");
        gloss.addColorStop(1,   "rgba(255,255,255,0.00)");
        ctx.fillStyle = gloss;
        ctx.beginPath(); ctx.roundRect(x + 2, y + 2, w - 4, 36, [r-2, r-2, 0, 0]); ctx.fill();

        const bottomSheen = ctx.createLinearGradient(x, y + h - 10, x, y + h);
        bottomSheen.addColorStop(0, "rgba(255,255,255,0)");
        bottomSheen.addColorStop(1, "rgba(255,255,255,0.28)");
        ctx.fillStyle = bottomSheen;
        ctx.beginPath(); ctx.roundRect(x + 2, y + h - 12, w - 4, 12, [0, 0, r-2, r-2]); ctx.fill();

        const screwPositions = [
            [x + 16, y + 18], [x + 16, y + h - 18],
            [x + w - 16, y + 18], [x + w - 16, y + h - 18]
        ];
        for (const [sx, sy] of screwPositions) {
            ctx.fillStyle   = "rgba(170,185,200,0.60)";
            ctx.strokeStyle = "rgba(145,162,180,0.48)";
            ctx.lineWidth   = 0.7;
            ctx.beginPath(); ctx.arc(sx, sy, 3.5, 0, Math.PI*2); ctx.fill(); ctx.stroke();
            ctx.fillStyle = "rgba(220,232,245,0.55)";
            ctx.beginPath(); ctx.arc(sx - 0.8, sy - 0.8, 1.4, 0, Math.PI*2); ctx.fill();
        }
    }


    const bug     = new Bug();
    const SPACING = 110;
    const letters = [
        new Letter("I", W/2 - SPACING*1.5,   0),
        new Letter("P", W/2 - SPACING*0.5,   0, true),
        new Letter("A", W/2 + SPACING*0.5, 210),
        new Letter("S", W/2 + SPACING*1.5, 440),
    ];

    function animate() {
        ctx.clearRect(0, 0, W, H);

        let sx = 0, sy = 0;
        if (shakeAmt > 0.06) {
            sx = (Math.random()-0.5) * shakeAmt;
            sy = (Math.random()-0.5) * shakeAmt * 0.45;
            shakeAmt *= shakeDecay || 0.80;
        } else {
            shakeAmt = 0;
        }

        ctx.save();
        if (sx || sy) ctx.translate(sx, sy);

        drawSignBoard();
        letters.forEach(l => l.update());
        letters.forEach(l => l.draw());
        bug.update();
        bug.draw();
        updateParticles();

        ctx.restore();
        requestAnimationFrame(animate);
    }

    setTimeout(() => requestAnimationFrame(animate), 400);
})();